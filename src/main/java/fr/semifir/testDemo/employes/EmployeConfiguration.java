package fr.semifir.testDemo.employes;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeConfiguration {

    @Bean
    public EmployeService employeService(
            EmployeRepository repository,
            ModelMapper mapper
    ) {
        return new EmployeService(repository, mapper);
    }

}
