@tag
Feature: Login
  	Como: Cliente
  	Quero: Fazer login na aplicação mobile
  	Para: Conseguir aceder à minha conta registada

  @tag1
  Scenario: AT1- Utilizador não insere nada nos campos
    Given a aplicação está pronta
    When clica no botão "Login"
    Then escreve no ecrã "Introduza o seu email." - 1

  @tag2
  Scenario: AT2- Utilizador não insere nada na palavra-passe
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And clica no botão "Login"
    Then escreve no ecrã "Introduza a sua palavra-passe." - 2

  @tag3
  Scenario: AT3- Email inserido não é válido
    Given a aplicação está pronta
    When utilizador insere "NotValid" no campo de email
    And utilizador insere "XPTO" no campo de palavra-passe
    And clica no botão "Login"
    Then escreve no ecrã "O email inserido não é válido!" - 3

  @tag4
  Scenario: AT4- Email inserido não está registado
    Given a aplicação está pronta
    When utilizador insere "teste@teste.pt" no campo de email
    And utilizador insere "XPTO" no campo de palavra-passe
    And clica no botão "Login"
    Then escreve no ecrã "O email inserido não está registado" - 4

  @tag5
  Scenario: AT5- Palavra-passe inserida está incorreta
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "NotValid" no campo de palavra-passe
    And clica no botão "Login"
    Then escreve no ecrã "A palavra-passe inserida está incorreta" - 5

  @tag6
  Scenario: AT6- Utilizador efetua o Login com sucesso
    Given a aplicação está pronta
    When utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "XPTO" no campo de palavra-passe
    And clica no botão "Login"
    Then é apresentada a página "Home"
