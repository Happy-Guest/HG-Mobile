@tag
Feature: Perfil de Utilizador
  Como: Cliente
  Quero: Aceder ao meu perfil registado
  Para: Ver os meus dados de utilizador

  @tag1
  Scenario: AT1- Utilizador conseguiu aceder ao seu Perfil
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    Then é apresentada a página "Perfil"
