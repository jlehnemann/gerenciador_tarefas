package br.com.jose.projeto.gerenciador_tarefas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jose.projeto.gerenciador_tarefas.model.Tarefa;
import br.com.jose.projeto.gerenciador_tarefas.repository.TarefaRepository;

@RestController // Anotação que combina @Controller e @ResponseBody. Diz ao Spring que esta classe é um controller REST e que os retornos dos métodos devem ser convertidos para JSON.
@RequestMapping("/api/tarefas") 
public class TarefaController {
	@Autowired // Injeção de Dependência: O Spring vai gerenciar a instância de TarefaRepository e "injetá-la" aqui para nós.
    private TarefaRepository tarefaRepository;

    // --- ENDPOINTS ---

    // 1. Criar uma tarefa (CREATE)
    // POST http://localhost:8080/api/tarefas
    @PostMapping
    public ResponseEntity<Tarefa> criarTarefa(@RequestBody Tarefa tarefa) {
        // @RequestBody: Pega o JSON do corpo da requisição e converte para um objeto Tarefa.
        Tarefa novaTarefa = tarefaRepository.save(tarefa);
        return new ResponseEntity<>(novaTarefa, HttpStatus.CREATED); // Retorna 201 Created com a tarefa criada no corpo.
    }

    // 2. Consultar todas as tarefas (READ)
    // GET http://localhost:8080/api/tarefas
    @GetMapping
    public List<Tarefa> listarTodasAsTarefas() {
        return tarefaRepository.findAll();
    }

    // 3. Consultar uma tarefa específica pelo ID (READ)
    // GET http://localhost:8080/api/tarefas/1
    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarTarefaPorId(@PathVariable Long id) {
        // @PathVariable: Pega o valor do 'id' da URL.
        Optional<Tarefa> tarefa = tarefaRepository.findById(id);

        if (tarefa.isPresent()) {
            return ResponseEntity.ok(tarefa.get()); // Retorna 200 OK com a tarefa.
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found.
        }
    }

    // 4. Atualizar uma tarefa existente (UPDATE)
    // PUT http://localhost:8080/api/tarefas/1
    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizarTarefa(@PathVariable Long id, @RequestBody Tarefa detalhesTarefa) {
        return tarefaRepository.findById(id)
                .map(tarefaExistente -> {
                    tarefaExistente.setNome(detalhesTarefa.getNome());
                    tarefaExistente.setDataEntrega(detalhesTarefa.getDataEntrega());
                    tarefaExistente.setResponsavel(detalhesTarefa.getResponsavel());
                    Tarefa tarefaAtualizada = tarefaRepository.save(tarefaExistente);
                    return ResponseEntity.ok(tarefaAtualizada);
                }).orElse(ResponseEntity.notFound().build());
    }
    
    // 5. Remover uma tarefa (DELETE)
    // DELETE http://localhost:8080/api/tarefas/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        if (!tarefaRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // Retorna 404 se a tarefa não existir
        }
        
        tarefaRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content, indicando sucesso na remoção.
    }
}
