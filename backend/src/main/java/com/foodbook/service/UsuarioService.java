package com.foodbook.service;

import com.foodbook.dto.request.AlterarSenhaRequest;
import com.foodbook.dto.request.AtualizarUsuarioRequest;
import com.foodbook.dto.response.UsuarioPerfilResponse;
import com.foodbook.dto.response.UsuarioResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.UsuarioMapper;
import com.foodbook.repository.CurtidaRepository;
import com.foodbook.repository.FavoritoRepository;
import com.foodbook.repository.ReceitaRepository;
import com.foodbook.repository.UsuarioRepository;
import com.foodbook.util.ImagemUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ReceitaRepository receitaRepository;
    private final CurtidaRepository curtidaRepository;
    private final FavoritoRepository favoritoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final ImagemUtil imagemUtil;

    @Transactional(readOnly = true)
    public UsuarioPerfilResponse buscarPerfil(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);

        long totalReceitas = receitaRepository.countByUsuarioId(usuarioId);
        long totalCurtidasRecebidas = curtidaRepository.countByReceitaUsuarioId(usuarioId);
        long totalFavoritos = favoritoRepository.countByIdUsuarioId(usuarioId);

        return new UsuarioPerfilResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFotoUrl(),
                totalReceitas,
                totalCurtidasRecebidas,
                totalFavoritos,
                usuario.getCriadoEm()
        );
    }

    @Transactional
    public UsuarioResponse atualizar(Long usuarioId, AtualizarUsuarioRequest request) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setNome(request.nome().trim());
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizarFoto(Long usuarioId, MultipartFile foto) {
        Usuario usuario = buscarPorId(usuarioId);

        if (usuario.getFotoUrl() != null) {
            imagemUtil.excluir(usuario.getFotoUrl());
        }

        String nomeArquivo = imagemUtil.salvar(foto);
        usuario.setFotoUrl(nomeArquivo);

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void alterarSenha(Long usuarioId, AlterarSenhaRequest request) {
        Usuario usuario = buscarPorId(usuarioId);

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw BusinessException.badRequest("Senha atual incorreta");
        }
        if (!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw BusinessException.badRequest("As senhas não coincidem");
        }

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
        log.info("Senha alterada para usuário {}", usuarioId);
    }

    @Transactional
    public void excluir(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        if (usuario.getFotoUrl() != null) {
            imagemUtil.excluir(usuario.getFotoUrl());
        }
        usuarioRepository.delete(usuario);
        log.info("Usuário {} excluído", usuarioId);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Usuário"));
    }
}
