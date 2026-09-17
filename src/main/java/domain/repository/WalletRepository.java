package domain.repository;

import domain.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) // пессимистичная блокировка, читать можно
    //SELECT * FROM wallet WHERE id = ? FOR UPDATE
    //Строка блокируется до конца транзакции. Другие транзакции, которые попытаются взять эту же строку
    //через PESSIMISTIC_WRITE, будут ждать, пока первая не закоммитится или не откатится
    @Query("SELECT w FROM Wallet w WHERE w.id =:id") //jpql-запрос
    Optional<Wallet> findByIdForUpdate(@Param("id") UUID id);

    //conditional update ч/з @Modifying - атомарный UPDATE - БД гарантирует атомарность,
    //compare-and-set на уровне БД
    // rowsAffected == 1 значит успех - возвращает кол-во измененных строк - 1 списание прошло, 0 не хватило ср-в или
    //кошелька нет
    @Modifying //говорит спринг дата, что это не селект, а модифицирующий запрос, предпочт. подход в высоконагруж сист.
    @Query("""
                UPDATE Wallet w
                SET w.balance=w.balance -:amount,
                    w.updatedAt =:now
                WHERE w.id =:id
                    AND w.balance - w.heldBalance >=:amount  /* учитывает заморож ср-ва */
            """)
    int debitIfEnough(@Param("id") UUID id,
                      @Param("amount") BigDecimal amount,
                      @Param("now") Instant now);
    //int - возвращ потому что сервис сам решает, что делать при 0
}
