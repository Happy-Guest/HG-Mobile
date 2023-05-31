@tag
Feature: Criar Conta
  Como: Cliente
  Quero: Criar uma conta na aplicação mobile
  Para: Obter uma conta com as minhas informações

  @tag1
  Scenario: AT1- Utilizador não tem internet
    Given a aplicação está pronta sem internet
    When clica no botão "Registar" no ecrã login
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "Sem ligação à internet!" - 1

  @tag2
  Scenario: AT2 - Não utilizador insere nada nos campos
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "Introduza o seu nome" - 9

  @tag3
  Scenario: AT3 - Nome inserido é muito pequeno
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XP" no campo do nome
    And utilizador insere "NotValid" no campo de email
    And utilizador insere "999999992" no campo de nº telefone
    And utilizador insere "XPTO" no campo de palavra-passe
    And utilizador insere "XPTO" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O nome tem de ser maior que 3 caracteres" 

  @tag4
  Scenario: AT4 - Email inserido não é válido
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "NotValid" no campo de email
    And utilizador insere "999999992" no campo de nº telefone
    And utilizador insere "XPTO" no campo de palavra-passe
    And utilizador insere "XPTO" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O email inserido não é válido" - 4

  @tag5
  Scenario: AT5 - Email inserido já se encontra registado
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "923456789" no campo de nº telefone
    And utilizador insere "XPTO" no campo de palavra-passe
    And utilizador insere "XPTO" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O email já esta a ser utilizado" 

  @tag6
  Scenario: AT6 - Número de telefone inserido não é válido
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "91" no campo de nº telefone
    And utilizador insere "XPTO" no campo de palavra-passe
    And utilizador insere "XPTO" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O número de telefone inserido não é válido" - 12

  @tag7
  Scenario: AT7 - Número de telefone inserido já se encontra registado
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPTO" no campo de palavra-passe
    And utilizador insere "XPTO" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "O número de telefone já está a ser utilizado" 

  @tag8
  Scenario: AT8 - palavra-passe inserida é muito pequena
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPT" no campo de palavra-passe
    And utilizador insere "XPT" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "A palavra-passe inserida é demasiado curta" - 14

  @tag9
  Scenario: AT9 - palavra-passe inserida não corresponde à confirmação
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPTO" no campo de palavra-passe
    And utilizador insere "XPT" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then escreve no ecrã "As confirmação da palavra-passe não corresponde" - 15

  @tag10
  Scenario: AT10 - Utilizador cria a Conta com sucesso
    Given a aplicação está pronta
    When clica no botão "Registar" no ecrã login
    And utilizador insere "XPTO" no campo do nome
    And utilizador insere "XPTO@mail.pt" no campo de email
    And utilizador insere "999999999" no campo de nº telefone
    And utilizador insere "XPTO" no campo de palavra-passe
    And utilizador insere "XPTO" no campo de confirmar palavra-passe
    And clica no botão "Registar" no ecrã registar
    Then é apresentado a página "Login"
