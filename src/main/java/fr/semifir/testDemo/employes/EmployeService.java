package fr.semifir.testDemo.employes;

import fr.semifir.testDemo.employes.dtos.EmployeDTO;
import org.modelmapper.ModelMapper;

import java.security.spec.ECParameterSpec;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EmployeService {

    EmployeRepository repository;
    ModelMapper mapper;

    public EmployeService(EmployeRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retourne une liste de collaborateurs
     * @return List<Employe>
     */
    public List<Employe> findAll() {
        return this.repository.findAll();
    }

    /**
     * Permet de retrouver un collaborateur
     * avec son ID
     * @param id Long
     * @return Optional<EmployeDTO>
     */
    public Optional<EmployeDTO> findById(final Long id) throws NoSuchElementException {
        Optional<Employe> employe = this.repository.findById(id);
        return Optional.of(mapper.map(employe.get(), EmployeDTO.class));
    }

    /**
     * Permet de persister un collaborateur
     * @param employeDTO EmployeDTO
     * @return EmployeDTO
     */
    public EmployeDTO save(EmployeDTO employeDTO) {
        Employe employe = mapper.map(employeDTO, Employe.class);
        Employe employeSaving = this.repository.save(employe);
        EmployeDTO response = mapper.map(employeSaving, EmployeDTO.class);
        return response;
    }

    /**
     * Permet de supprimer un collaborateur
     * @param employeDTO EmployeDTO
     */
    public void delete(EmployeDTO employeDTO) {
        Employe employe = mapper.map(employeDTO, Employe.class);
        this.repository.delete(employe);
    }
}
