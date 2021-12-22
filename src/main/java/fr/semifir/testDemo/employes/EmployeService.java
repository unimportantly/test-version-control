package fr.semifir.testDemo.employes;

import fr.semifir.testDemo.employes.dtos.EmployeDTO;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EmployeService {

    private EmployeRepository repository;
    private ModelMapper mapper;

    public EmployeService(EmployeRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retourne une liste de collaborateurs
     * @return List<EmployeDTO>
     */
    public List<EmployeDTO> findAll() {
        List<EmployeDTO> employeDTOList = new ArrayList<>();
        this.repository.findAll().forEach(employe -> {
            employeDTOList.add(mapper.map(employe, EmployeDTO.class));
        });
        return employeDTOList;
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
