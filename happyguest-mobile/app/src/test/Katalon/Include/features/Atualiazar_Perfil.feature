@tag
Feature: Atualizar Perfil
  Como: Cliente
  Quero: Alterar o meu perfil de utilizador
  Para: Atualizar os meus dados de utilizador

  @tag1
  Scenario: AT1- Utilizador limpa os campos
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere " " no campo do nome perfil
    And utilizador insere " " no campo de email perfil
    And utilizador insere " " no campo de nº telefone perfil
    And utilizador insere " " no campo de morada perfil
    And utilizador insere " " no campo de data de nascimento perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "Insira o seu nome" - 9

  @tag2
  Scenario: AT2- Nome vazio
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere " " no campo do nome perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "Introduza o seu nome" - 9

  @tag3
  Scenario: AT3- Nome demasiado curto
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "XP" no campo do nome perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "O nome é demasiado curto" - 10

  @tag4
  Scenario: AT4- Email vazio
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere " " no campo de email perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "Insira o seu email" - 2

  @tag5
  Scenario: AT5- Email inválido
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "NotValid" no campo de email perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "O emai não é válido" - 4

  @tag6
  Scenario: AT6- Email utilizado
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "tomasn@happyguest.pt" no campo de email perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "O email já se encontra registado"

  @tag7
  Scenario: AT7 - Número de telefone inválido
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "91" no campo de nº telefone perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "O Nº telefone não é válido" - 11

  @tag8
  Scenario: AT8 - Morada demasiado curta
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "rua" no campo de morada perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "A morada é demasiado curta"

  @tag9
  Scenario: AT9 - Data de nascimento inválido
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "12" no campo de data de nascimento perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "A data de nascimento não é válida"

  @tag10
  Scenario: AT10 - Data de nascimento formato incorreto
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "32/13/2024" no campo de data de nascimento perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "A data de nascimento para o campo data de nascimento não respeita o formato Y/m/d"

  @tag11
  Scenario: AT11 - Data de nascimento fora de tempo
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "12/12/2024" no campo de data de nascimento perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then escreve no ecrã "O campo data de nascimento deverá conter uma data anterior a hoje"

  @tag12
  Scenario: AT12- Utilizador consegue atualizar os dados
    Given a aplicação está pronta
    When utilizador clica no icon de perfil
    And utilizador clica no ícone de Editar
    And utilizador insere "XPTO1" no campo do nome
    And utilizador insere "xpto1@mail.pt" no campo de email
    And utilizador insere "912345678" no campo de nº telefone
    And utilizador insere "Rua xxx" no campo de morada perfil
    And utilizador insere "14/01/2002" no campo de data de nascimento perfil
    And utilizador clica no botão “Atualizar Perfil“
    Then é apresentado a página "Perfil" atualizada
