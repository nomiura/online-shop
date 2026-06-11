package domain.account.repository;

import domain.account.entity.Account;
import domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//HTTP Request (браузер) → Controller → Service → Repository → Database
//И обратно:
//Database → Repository → Service → Controller → HTTP Response (браузер)

//DAO DATA ACCESS OBJECT - логика прямого взаимодействия с бд, CRUD-операции, запросы

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
//    accountRepository.save(account);           // сохранить/обновить
//    accountRepository.findById(id);            // найти по ID
//    accountRepository.findAll();               // все записи
//    accountRepository.deleteById(id);          // удалить
//    accountRepository.count();                 // количество
//    accountRepository.existsById(id);          // существует?
//    accountRepository.findAll(Sort.by("name")); // сортировка
//    accountRepository.findAll(PageRequest.of(0, 10)); // пагинация
    Optional<Account> findByEmail(String email);


    boolean existsByEmail(String email);
}
