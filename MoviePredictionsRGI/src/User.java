
public class User {
	
	int idade;
	boolean sexo;
	String profissao;
	
	
	public User()
	{
		idade=0;
		sexo=false;
		profissao=null;
	}
	
	public User(int idadeUser, boolean sexoUser, String prof)
	{
		idade=idadeUser;
		sexo=sexoUser;
		profissao=prof;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public boolean isSexo() {
		return sexo;
	}

	public void setSexo(boolean sexo) {
		this.sexo = sexo;
	}

	public String getProfissao() {
		return profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

}
