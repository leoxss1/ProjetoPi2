
const formulario = document.getElementById("formProduto");

const API_URL = "http://localhost:8080/api/produtos";

formulario.addEventListener("submit", async function (event) {

    event.preventDefault();


    const nome = document.getElementById("nome").value.trim();

    const preco = parseFloat(
        document.getElementById("preco").value
    );

    const quantidade = parseInt(
        document.getElementById("quantidade").value
    );

    const descricao = document.getElementById("descricao").value.trim();

    const dataValidade = document.getElementById("dataValidade").value;

     const tipoProduto = document.getElementById("tipoProduto").value;



    if (!nome || !descricao || !dataValidade || isNaN(preco)) {
        alert("Preencha todos os campos corretamente!");
        return;
    }

    if (preco <= 0) {
        alert("O preço deve ser maior que zero!");
        return;
    }


    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    const validade = new Date(dataValidade + "T00:00:00");

    if (validade < hoje) {
        alert("A data de validade não pode ser anterior à data atual!");
        return;
    }

    const produto = {
        nome: nome,
        preco: preco,
        descricao: descricao,
        quantidade: quantidade,
        dataValidade: dataValidade,
        tipoProduto: tipoProduto
    };


    try {

        // Enviando produto para o Spring Boot
        const resposta = await fetch(API_URL, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(produto)

        });


        // Verificando resposta da API
        if (resposta.ok) {

            alert("Produto cadastrado com sucesso!");

            // Limpando formulário
            formulario.reset();

        } else {

            const erro = await resposta.text();

            console.error("Erro retornado pela API:", erro);

            alert("Não foi possível cadastrar o produto!");

        }

    } catch (erro) {

        console.error("Erro na comunicação com o servidor:", erro);

        alert(
            "Erro ao conectar com o servidor. " +
            "Verifique se o Spring Boot está iniciado."
        );

    }

});
