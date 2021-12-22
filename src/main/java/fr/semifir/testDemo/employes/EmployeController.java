package fr.semifir.testDemo.employes;

import fr.semifir.testDemo.employes.dtos.EmployeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("employees")
public class EmployeController {

    @Autowired
    private EmployeService service;

    @GetMapping
    public List<EmployeDTO> findAll() {
        return this.service.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeDTO> findById(@PathVariable Long id) {
        try {
            Optional<EmployeDTO> employeDTO = this.service.findById(id);
            return ResponseEntity.ok(employeDTO.get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @PostMapping
    public ResponseEntity<EmployeDTO> save(@RequestBody EmployeDTO employeDTO) {
        EmployeDTO response = this.service.save(employeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<EmployeDTO> update(@RequestBody EmployeDTO employeDTO) {
        EmployeDTO response = this.service.save(employeDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody EmployeDTO employeDTO) {
        this.service.delete(employeDTO);
        return ResponseEntity.ok(true);
    }
}
