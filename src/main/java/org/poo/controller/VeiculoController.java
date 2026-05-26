package org.poo.controller;

import org.poo.model.Veiculo;
import org.poo.repository.VeiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoRepository repository = new VeiculoRepository();

    @GetMapping
    public List<Veiculo> getAll() throws SQLException {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Veiculo veiculo) throws SQLException {
        repository.save(veiculo);
    }
}
