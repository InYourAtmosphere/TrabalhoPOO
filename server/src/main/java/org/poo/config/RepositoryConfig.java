package org.poo.config;

import org.poo.repository.AuthenticationTokenRepository;
import org.poo.repository.ClienteRepository;
import org.poo.repository.ContratoRepository;
import org.poo.repository.FuncionarioRepository;
import org.poo.repository.ManutencaoRepository;
import org.poo.repository.UnidadeRepository;
import org.poo.repository.VeiculoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public UnidadeRepository unidadeRepository() {
        return new UnidadeRepository();
    }

    @Bean
    public ClienteRepository clienteRepository() {
        return new ClienteRepository();
    }

    @Bean
    public VeiculoRepository veiculoRepository(UnidadeRepository unidadeRepository) {
        return new VeiculoRepository(unidadeRepository);
    }

    @Bean
    public FuncionarioRepository funcionarioRepository(UnidadeRepository unidadeRepository) {
        return new FuncionarioRepository(unidadeRepository);
    }

    @Bean
    public ContratoRepository contratoRepository(ClienteRepository clienteRepository,
                                                   VeiculoRepository veiculoRepository,
                                                   UnidadeRepository unidadeRepository) {
        return new ContratoRepository(clienteRepository, veiculoRepository, unidadeRepository);
    }

    @Bean
    public ManutencaoRepository manutencaoRepository(VeiculoRepository veiculoRepository) {
        return new ManutencaoRepository(veiculoRepository);
    }

    @Bean
    public AuthenticationTokenRepository authenticationTokenRepository(FuncionarioRepository funcionarioRepository) {
        return new AuthenticationTokenRepository(funcionarioRepository);
    }
}
