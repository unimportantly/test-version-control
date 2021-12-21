package fr.semifir.testDemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.semifir.testDemo.employes.EmployeController;
import fr.semifir.testDemo.employes.EmployeService;
import fr.semifir.testDemo.employes.dtos.EmployeDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.Optional;

@WebMvcTest(controllers = EmployeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeService service;

    @Test
    public void testFindAllEmployees() throws Exception{
        this.mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testFindOneEmployeeWhereWrongIdOrInexistantEmployee() throws Exception {
        this.mockMvc.perform(get("/employees/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindOneEmployee() throws Exception {
        EmployeDTO employeDTO = this.employeDTO();
        BDDMockito.given(service.findById(1L))
                .willReturn(Optional.of(employeDTO));
        MvcResult result = this.mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andReturn();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        EmployeDTO body = json.fromJson(result.getResponse().getContentAsString(), EmployeDTO.class);
        Assertions.assertEquals(body.getUsername(), this.employeDTO().getUsername());
        Assertions.assertEquals(body.getId(), this.employeDTO().getId());
    }

    @Test
    public void testSaveEmployees() throws Exception {
        EmployeDTO employeDTO = this.employeDTO();
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(employeDTO);
        this.mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateEmployees() throws Exception {
        // 1 il faut récup un employé
        EmployeDTO employeDTO = this.employeDTO();
        EmployeDTO employeDTOUpdated = this.employeDTOUpdate();

        BDDMockito.given(service.findById(1L))
                .willReturn(Optional.of(employeDTO));

        MvcResult result = this.mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andReturn();

        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        EmployeDTO body = json.fromJson(result.getResponse().getContentAsString(), EmployeDTO.class);

        BDDMockito.when(service.save(any(EmployeDTO.class)))
                .thenReturn(employeDTOUpdated);

        // 2 il faut le modifier
        body.setUsername("toto");
        String bodyToSave = json.toJson(body);
        MvcResult resultUpdated = this.mockMvc.perform(put("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSave))
                        .andExpect(status().isOk())
                        .andReturn();

        // 2.5 on transforme le résultat en Objet
        EmployeDTO finalBody = json.fromJson(resultUpdated.getResponse().getContentAsString(), EmployeDTO.class);
        // 3 on verifie si il a bien été modifie
        Assertions.assertEquals(finalBody.getUsername(), this.employeDTOUpdate().getUsername());
    }

    @Test
    public void testDeleteEmploye() throws Exception {
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(this.employeDTO());
        this.mockMvc.perform(delete("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isOk());
    }

    private EmployeDTO employeDTO() {
        return new EmployeDTO(
                5L,
                "antoine",
                "antoine&semifir.com",
                new Date(),
                1,
                3456F
        );
    }

    private EmployeDTO employeDTOUpdate() {
        return new EmployeDTO(
                5L,
                "toto",
                "antoine&semifir.com",
                new Date(),
                1,
                3456F
        );
    }
}
