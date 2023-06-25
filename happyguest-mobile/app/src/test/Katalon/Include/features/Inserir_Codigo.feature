@tag
Feature: Inserir Código de Acesso
  Como: Cliente
  Quero: Inserir o meu código de acesso da reserva
  Para: Associar o código da reserva à minha conta

  @tag1
  Scenario: AT1- Utilizador não insere nada
    Given a aplicação está pronta
    When utilizador clica no botão “Associar“
    Then escreve no ecrã "Introduza o código"

  @tag2
  Scenario: AT2- Insere um código inválido
    Given a aplicação está pronta
    When utilizador insere "NotValid" no campo de código 
    And utilizador clica no botão “Associar“
    Then escreve no ecrã "O código não é válido"

  @tag3
  Scenario: AT3- Insere um código expirado
    Given a aplicação está pronta
    When utilizador insere "A1352" no campo de código 
    And utilizador clica no botão “Associar“
    Then escreve no ecrã "Código já está expirado"

  @tag4
  Scenario: AT4- Utilizador consegue inserir um código
    Given a aplicação está pronta
    When utilizador insere "C1CH2" no campo de código 
    And utilizador clica no botão “Associar“
    Then escreve no ecrã "Código associado com sucesso"
