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

    // On injecte MockMvc
    // MockMvc permet de simuler un composent
    @Autowired
    private MockMvc mockMvc;

    // On mock le service
    // On récupère le service
    // On fait une "copie" du service
    @MockBean
    private EmployeService service;

    /**
     * On va tester la route qui permet de récuperer un tableau
     * de collaborateurs
     * @throws Exception
     */
    @Test
    public void testFindAllEmployees() throws Exception{
        // On execute une request sur /employees
        // grâce à la methode "perfom" de mockMvc
        this.mockMvc.perform(get("/employees"))
                .andExpect(status().isOk()) // On check si le code retour et 200
                .andExpect(jsonPath("$").isEmpty()); // On verifie si le tableau est vide
    }

    /**
     * On vérifie si la route qui permet de retrouver un collabo
     * renvoi bien un 404 si on ne trouve pas de collabo
     * @throws Exception
     */
    @Test
    public void testFindOneEmployeeWhereWrongIdOrInexistantEmployee() throws Exception {
        // On execute une request sur /employees/1
        // grâce à la methode "perfom" de mockMvc
        this.mockMvc.perform(get("/employees/1"))
                .andExpect(status().isNotFound()); // On vérifie si le status est bien une 404
    }

    /**
     * On vérifie si la route qui permet de retrouver un collabo
     * renvoi bien un collabo
     * @throws Exception
     */
    @Test
    public void testFindOneEmployee() throws Exception {
        /**
         * START
         * On mock le service pour qu'il renvoi un employeDTO
         * On simule l"'existance d'un employeDTO dans le BDD
         */
        /// On créé un employeDTO
        EmployeDTO employeDTO = this.employeDTO();
        // On appelle la methode “given" de BDDMockito pour mocker le service
        // On lui passe en parametre quelle méthode du service il faut mocker
        BDDMockito.given(service.findById(1L))
                .willReturn(Optional.of(employeDTO)); // On indique à BDDMockito : "que va tu répondre quand on appelle cette methode ?"
        // Et on lui passe en paramètre sa réponse
        /**
         * END
         */
        /// On test notre route qui permet de récuperer un collabo
        MvcResult result = this.mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andReturn(); // On va stocker le résultat du mockMvc dans une variable
                                // cela va nous permettre de traiter le resultat
        // On initialise un objet Gson qui va nous permettre de manipuler/transformer
        // des objets en JSON ou l'inverse
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        // On transform notre JSON en EmployeDTO
        EmployeDTO body = json.fromJson(
                // Je récupère le contenu de mon MvcResult,
                // j'accede à la réponse : "getResponse"
                // Je récupère le contenu du body de la réponse sous forme de String getContentAsString
                result.getResponse().getContentAsString(),
                // Je dis à Gson en quel objet il faut le transformer
                EmployeDTO.class
        );
        // Je test
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

        // On indique à BDDMockito :
        // Quand on taope sur la méthode save du service avec
        // N'importe quel objet qui est de type EmployeDTO
        BDDMockito.when(service.save(any(EmployeDTO.class)))
                .thenReturn(employeDTOUpdated); // On retourne un employeDTOUpdated

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
                        .contentType(MediaType.APPLICATION_JSON) // Le type de données que l'on passe dans notre request
                        .content(body)) // Le contenu du body
                        .andExpect(status().isOk());
    }

    // Les deux méthodes ci-dessous nous permettent de créer un employé DTO
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
