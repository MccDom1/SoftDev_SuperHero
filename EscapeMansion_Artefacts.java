public class EscapeMansion_Artefacts
{
	//Check text files to reformat enemy drops if needed (I'm a bit lost on how to do it)
	
	private String name;
	private String type;
	private String description;
	private int health;
	private int damage;
	
	public EscapeMansion_Artefacts(String name, String type, String description, int health, int damage)
	{
		this.name = name;
		this.type = type;
		this.description = description;
		this.health = health;
		this.damage = damage;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getDamage()
	{
		return damage;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
