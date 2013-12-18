
public class Item {
	
	String titulo;
	String data;
	String linkImdb;
	String genero;
	
	
	public Item(String tit, String date, String link, String genero)
	{
		titulo = tit;
		data = date;
		linkImdb = link;
		genero = genero;
	}


	public String getTitulo() {
		return titulo;
	}


	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public String getLinkImdb() {
		return linkImdb;
	}


	public void setLinkImdb(String linkImdb) {
		this.linkImdb = linkImdb;
	}


	public String getGenero() {
		return genero;
	}


	public void setGenero(String genero) {
		this.genero = genero;
	}

}
