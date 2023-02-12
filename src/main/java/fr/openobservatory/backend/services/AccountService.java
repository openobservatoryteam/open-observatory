package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.RegisterUserDto;
import fr.openobservatory.backend.exceptions.UsernameAlreadyUsedException;
import fr.openobservatory.backend.models.Account;
import fr.openobservatory.backend.models.AccountType;
import fr.openobservatory.backend.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account registerAccount(RegisterUserDto accountDto) throws UsernameAlreadyUsedException {
        if (accountRepository.findByUsername(accountDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyUsedException();
        }
        Account account = new Account();
        account.setUsername(accountDto.getUsername());
        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        account.setBiography(accountDto.getBiography());
        account.setPublicProfil(true);
        account.setType(AccountType.USER);
        account.setAvatarUrl("test");
        account.setCreated_at(new Date());
        return accountRepository.save(account);
    }



}
