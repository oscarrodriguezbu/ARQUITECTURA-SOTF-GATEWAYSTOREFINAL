package com.themakers.storeonline.web.rest;

import com.themakers.storeonline.GatewaystoreApp;
import com.themakers.storeonline.domain.Usuario;
import com.themakers.storeonline.domain.User;
import com.themakers.storeonline.repository.UsuarioRepository;
import com.themakers.storeonline.service.UsuarioService;
import com.themakers.storeonline.service.dto.UsuarioDTO;
import com.themakers.storeonline.service.mapper.UsuarioMapper;
import com.themakers.storeonline.service.dto.UsuarioCriteria;
import com.themakers.storeonline.service.UsuarioQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UsuarioResource} REST controller.
 */
@SpringBootTest(classes = GatewaystoreApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class UsuarioResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_DIRECCION = "AAAAAAAAAA";
    private static final String UPDATED_DIRECCION = "BBBBBBBBBB";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioQueryService usuarioQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUsuarioMockMvc;

    private Usuario usuario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Usuario createEntity(EntityManager em) {
        Usuario usuario = new Usuario()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .direccion(DEFAULT_DIRECCION);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        usuario.setUser(user);
        return usuario;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Usuario createUpdatedEntity(EntityManager em) {
        Usuario usuario = new Usuario()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .direccion(UPDATED_DIRECCION);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        usuario.setUser(user);
        return usuario;
    }

    @BeforeEach
    public void initTest() {
        usuario = createEntity(em);
    }

    @Test
    @Transactional
    public void createUsuario() throws Exception {
        int databaseSizeBeforeCreate = usuarioRepository.findAll().size();

        // Create the Usuario
        UsuarioDTO usuarioDTO = usuarioMapper.toDto(usuario);
        restUsuarioMockMvc.perform(post("/api/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(usuarioDTO)))
            .andExpect(status().isCreated());

        // Validate the Usuario in the database
        List<Usuario> usuarioList = usuarioRepository.findAll();
        assertThat(usuarioList).hasSize(databaseSizeBeforeCreate + 1);
        Usuario testUsuario = usuarioList.get(usuarioList.size() - 1);
        assertThat(testUsuario.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testUsuario.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testUsuario.getDireccion()).isEqualTo(DEFAULT_DIRECCION);
    }

    @Test
    @Transactional
    public void createUsuarioWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = usuarioRepository.findAll().size();

        // Create the Usuario with an existing ID
        usuario.setId(1L);
        UsuarioDTO usuarioDTO = usuarioMapper.toDto(usuario);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUsuarioMockMvc.perform(post("/api/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(usuarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Usuario in the database
        List<Usuario> usuarioList = usuarioRepository.findAll();
        assertThat(usuarioList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllUsuarios() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList
        restUsuarioMockMvc.perform(get("/api/usuarios?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(usuario.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION)));
    }
    
    @Test
    @Transactional
    public void getUsuario() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get the usuario
        restUsuarioMockMvc.perform(get("/api/usuarios/{id}", usuario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(usuario.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO))
            .andExpect(jsonPath("$.direccion").value(DEFAULT_DIRECCION));
    }


    @Test
    @Transactional
    public void getUsuariosByIdFiltering() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        Long id = usuario.getId();

        defaultUsuarioShouldBeFound("id.equals=" + id);
        defaultUsuarioShouldNotBeFound("id.notEquals=" + id);

        defaultUsuarioShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUsuarioShouldNotBeFound("id.greaterThan=" + id);

        defaultUsuarioShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUsuarioShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllUsuariosByNombreIsEqualToSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where nombre equals to DEFAULT_NOMBRE
        defaultUsuarioShouldBeFound("nombre.equals=" + DEFAULT_NOMBRE);

        // Get all the usuarioList where nombre equals to UPDATED_NOMBRE
        defaultUsuarioShouldNotBeFound("nombre.equals=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    public void getAllUsuariosByNombreIsNotEqualToSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where nombre not equals to DEFAULT_NOMBRE
        defaultUsuarioShouldNotBeFound("nombre.notEquals=" + DEFAULT_NOMBRE);

        // Get all the usuarioList where nombre not equals to UPDATED_NOMBRE
        defaultUsuarioShouldBeFound("nombre.notEquals=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    public void getAllUsuariosByNombreIsInShouldWork() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where nombre in DEFAULT_NOMBRE or UPDATED_NOMBRE
        defaultUsuarioShouldBeFound("nombre.in=" + DEFAULT_NOMBRE + "," + UPDATED_NOMBRE);

        // Get all the usuarioList where nombre equals to UPDATED_NOMBRE
        defaultUsuarioShouldNotBeFound("nombre.in=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    public void getAllUsuariosByNombreIsNullOrNotNull() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where nombre is not null
        defaultUsuarioShouldBeFound("nombre.specified=true");

        // Get all the usuarioList where nombre is null
        defaultUsuarioShouldNotBeFound("nombre.specified=false");
    }
                @Test
    @Transactional
    public void getAllUsuariosByNombreContainsSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where nombre contains DEFAULT_NOMBRE
        defaultUsuarioShouldBeFound("nombre.contains=" + DEFAULT_NOMBRE);

        // Get all the usuarioList where nombre contains UPDATED_NOMBRE
        defaultUsuarioShouldNotBeFound("nombre.contains=" + UPDATED_NOMBRE);
    }

    @Test
    @Transactional
    public void getAllUsuariosByNombreNotContainsSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where nombre does not contain DEFAULT_NOMBRE
        defaultUsuarioShouldNotBeFound("nombre.doesNotContain=" + DEFAULT_NOMBRE);

        // Get all the usuarioList where nombre does not contain UPDATED_NOMBRE
        defaultUsuarioShouldBeFound("nombre.doesNotContain=" + UPDATED_NOMBRE);
    }


    @Test
    @Transactional
    public void getAllUsuariosByApellidoIsEqualToSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where apellido equals to DEFAULT_APELLIDO
        defaultUsuarioShouldBeFound("apellido.equals=" + DEFAULT_APELLIDO);

        // Get all the usuarioList where apellido equals to UPDATED_APELLIDO
        defaultUsuarioShouldNotBeFound("apellido.equals=" + UPDATED_APELLIDO);
    }

    @Test
    @Transactional
    public void getAllUsuariosByApellidoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where apellido not equals to DEFAULT_APELLIDO
        defaultUsuarioShouldNotBeFound("apellido.notEquals=" + DEFAULT_APELLIDO);

        // Get all the usuarioList where apellido not equals to UPDATED_APELLIDO
        defaultUsuarioShouldBeFound("apellido.notEquals=" + UPDATED_APELLIDO);
    }

    @Test
    @Transactional
    public void getAllUsuariosByApellidoIsInShouldWork() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where apellido in DEFAULT_APELLIDO or UPDATED_APELLIDO
        defaultUsuarioShouldBeFound("apellido.in=" + DEFAULT_APELLIDO + "," + UPDATED_APELLIDO);

        // Get all the usuarioList where apellido equals to UPDATED_APELLIDO
        defaultUsuarioShouldNotBeFound("apellido.in=" + UPDATED_APELLIDO);
    }

    @Test
    @Transactional
    public void getAllUsuariosByApellidoIsNullOrNotNull() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where apellido is not null
        defaultUsuarioShouldBeFound("apellido.specified=true");

        // Get all the usuarioList where apellido is null
        defaultUsuarioShouldNotBeFound("apellido.specified=false");
    }
                @Test
    @Transactional
    public void getAllUsuariosByApellidoContainsSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where apellido contains DEFAULT_APELLIDO
        defaultUsuarioShouldBeFound("apellido.contains=" + DEFAULT_APELLIDO);

        // Get all the usuarioList where apellido contains UPDATED_APELLIDO
        defaultUsuarioShouldNotBeFound("apellido.contains=" + UPDATED_APELLIDO);
    }

    @Test
    @Transactional
    public void getAllUsuariosByApellidoNotContainsSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where apellido does not contain DEFAULT_APELLIDO
        defaultUsuarioShouldNotBeFound("apellido.doesNotContain=" + DEFAULT_APELLIDO);

        // Get all the usuarioList where apellido does not contain UPDATED_APELLIDO
        defaultUsuarioShouldBeFound("apellido.doesNotContain=" + UPDATED_APELLIDO);
    }


    @Test
    @Transactional
    public void getAllUsuariosByDireccionIsEqualToSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where direccion equals to DEFAULT_DIRECCION
        defaultUsuarioShouldBeFound("direccion.equals=" + DEFAULT_DIRECCION);

        // Get all the usuarioList where direccion equals to UPDATED_DIRECCION
        defaultUsuarioShouldNotBeFound("direccion.equals=" + UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    public void getAllUsuariosByDireccionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where direccion not equals to DEFAULT_DIRECCION
        defaultUsuarioShouldNotBeFound("direccion.notEquals=" + DEFAULT_DIRECCION);

        // Get all the usuarioList where direccion not equals to UPDATED_DIRECCION
        defaultUsuarioShouldBeFound("direccion.notEquals=" + UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    public void getAllUsuariosByDireccionIsInShouldWork() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where direccion in DEFAULT_DIRECCION or UPDATED_DIRECCION
        defaultUsuarioShouldBeFound("direccion.in=" + DEFAULT_DIRECCION + "," + UPDATED_DIRECCION);

        // Get all the usuarioList where direccion equals to UPDATED_DIRECCION
        defaultUsuarioShouldNotBeFound("direccion.in=" + UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    public void getAllUsuariosByDireccionIsNullOrNotNull() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where direccion is not null
        defaultUsuarioShouldBeFound("direccion.specified=true");

        // Get all the usuarioList where direccion is null
        defaultUsuarioShouldNotBeFound("direccion.specified=false");
    }
                @Test
    @Transactional
    public void getAllUsuariosByDireccionContainsSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where direccion contains DEFAULT_DIRECCION
        defaultUsuarioShouldBeFound("direccion.contains=" + DEFAULT_DIRECCION);

        // Get all the usuarioList where direccion contains UPDATED_DIRECCION
        defaultUsuarioShouldNotBeFound("direccion.contains=" + UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    public void getAllUsuariosByDireccionNotContainsSomething() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        // Get all the usuarioList where direccion does not contain DEFAULT_DIRECCION
        defaultUsuarioShouldNotBeFound("direccion.doesNotContain=" + DEFAULT_DIRECCION);

        // Get all the usuarioList where direccion does not contain UPDATED_DIRECCION
        defaultUsuarioShouldBeFound("direccion.doesNotContain=" + UPDATED_DIRECCION);
    }


    @Test
    @Transactional
    public void getAllUsuariosByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = usuario.getUser();
        usuarioRepository.saveAndFlush(usuario);
        Long userId = user.getId();

        // Get all the usuarioList where user equals to userId
        defaultUsuarioShouldBeFound("userId.equals=" + userId);

        // Get all the usuarioList where user equals to userId + 1
        defaultUsuarioShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUsuarioShouldBeFound(String filter) throws Exception {
        restUsuarioMockMvc.perform(get("/api/usuarios?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(usuario.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION)));

        // Check, that the count call also returns 1
        restUsuarioMockMvc.perform(get("/api/usuarios/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUsuarioShouldNotBeFound(String filter) throws Exception {
        restUsuarioMockMvc.perform(get("/api/usuarios?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUsuarioMockMvc.perform(get("/api/usuarios/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingUsuario() throws Exception {
        // Get the usuario
        restUsuarioMockMvc.perform(get("/api/usuarios/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUsuario() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        int databaseSizeBeforeUpdate = usuarioRepository.findAll().size();

        // Update the usuario
        Usuario updatedUsuario = usuarioRepository.findById(usuario.getId()).get();
        // Disconnect from session so that the updates on updatedUsuario are not directly saved in db
        em.detach(updatedUsuario);
        updatedUsuario
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .direccion(UPDATED_DIRECCION);
        UsuarioDTO usuarioDTO = usuarioMapper.toDto(updatedUsuario);

        restUsuarioMockMvc.perform(put("/api/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(usuarioDTO)))
            .andExpect(status().isOk());

        // Validate the Usuario in the database
        List<Usuario> usuarioList = usuarioRepository.findAll();
        assertThat(usuarioList).hasSize(databaseSizeBeforeUpdate);
        Usuario testUsuario = usuarioList.get(usuarioList.size() - 1);
        assertThat(testUsuario.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testUsuario.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testUsuario.getDireccion()).isEqualTo(UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    public void updateNonExistingUsuario() throws Exception {
        int databaseSizeBeforeUpdate = usuarioRepository.findAll().size();

        // Create the Usuario
        UsuarioDTO usuarioDTO = usuarioMapper.toDto(usuario);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUsuarioMockMvc.perform(put("/api/usuarios")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(usuarioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Usuario in the database
        List<Usuario> usuarioList = usuarioRepository.findAll();
        assertThat(usuarioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUsuario() throws Exception {
        // Initialize the database
        usuarioRepository.saveAndFlush(usuario);

        int databaseSizeBeforeDelete = usuarioRepository.findAll().size();

        // Delete the usuario
        restUsuarioMockMvc.perform(delete("/api/usuarios/{id}", usuario.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Usuario> usuarioList = usuarioRepository.findAll();
        assertThat(usuarioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
