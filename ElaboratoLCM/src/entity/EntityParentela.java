package entity;

public class EntityParentela {

	private EntityGenitore eg;
	private EntityAlunno ea;
	
	public void associaGenitore( EntityGenitore g) {
		this.eg=g;
	}
	
	public void associaAlunno( EntityAlunno a) {
		this.ea=a;
	}
}
