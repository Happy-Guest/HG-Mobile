@tag
Feature: Terminar Sessão
  Como: Cliente
  Quero: Terminar a minha sessão na aplicação mobile
  Para: Não deixar a minha sessão iniciada

  @tag1
  Scenario: AT1- Utilizador efetua o Logout com sucesso
    Given a aplicação está pronta e tem sessão iniciada
    When clica na sidebar
    And clica no botão "Sair"
    Then é apresentada a página "Login"
