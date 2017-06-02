/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifs.trabalhowebsocket.transfer;

/**
 *
 * @author neetocode
 */
public class Usuario {
    private int id;
    private String nome;
    private String senha;

    public Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    
    public Usuario() {
        
    }
    
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
   

}
