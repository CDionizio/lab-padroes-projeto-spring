package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ViaCepService viaCepService;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              EnderecoRepository enderecoRepository,
                              ViaCepService viaCepService) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.viaCepService = viaCepService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado: " + id));
    }

    @Override
    public Cliente inserir(Cliente cliente) {
        cliente.setId(null);
        return salvarClienteComCep(cliente);
    }

    @Override
    public Cliente atualizar(Long id, Cliente cliente) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id);
        }
        cliente.setId(id);
        return salvarClienteComCep(cliente);
    }

    @Override
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private Cliente salvarClienteComCep(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do cliente é obrigatório");
        }
        if (cliente.getEndereco() == null || cliente.getEndereco().getCep() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O CEP do cliente é obrigatório");
        }

        String cepInformado = cliente.getEndereco().getCep().trim();
        if (!cepInformado.matches("\\d{5}-?\\d{3}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido");
        }
        String cep = cepInformado.replace("-", "");

        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            Endereco consultado = viaCepService.consultarCep(cep);
            if (consultado == null || Boolean.TRUE.equals(consultado.getErro())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado: " + cep);
            }
            consultado.setCep(cep);
            return enderecoRepository.save(consultado);
        });

        cliente.setEndereco(endereco);
        return clienteRepository.save(cliente);
    }
}
