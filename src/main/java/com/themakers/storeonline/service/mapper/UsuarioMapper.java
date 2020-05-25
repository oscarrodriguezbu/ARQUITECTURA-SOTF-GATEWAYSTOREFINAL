package com.themakers.storeonline.service.mapper;


import com.themakers.storeonline.domain.*;
import com.themakers.storeonline.service.dto.UsuarioDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Usuario} and its DTO {@link UsuarioDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UsuarioMapper extends EntityMapper<UsuarioDTO, Usuario> {

    @Mapping(source = "user.id", target = "userId")
    UsuarioDTO toDto(Usuario usuario);

    @Mapping(source = "userId", target = "user")
    Usuario toEntity(UsuarioDTO usuarioDTO);

    default Usuario fromId(Long id) {
        if (id == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setId(id);
        return usuario;
    }
}
