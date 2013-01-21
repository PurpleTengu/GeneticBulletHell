import java.util.ArrayList;

public class FireCommand extends Command{
	protected ArrayList<BulletSeed> bullets = new ArrayList<BulletSeed>(0);
	
	public FireCommand(ArrayList<BulletSeed> bullets){
		this.bullets=bullets;
		
	}
	
	public void apply(Boss boss){
		
		for(int i=0;i<bullets.size();i++){
			if(i>boss.volleySize || boss.MP<bullets.get(i).power/1000){break;}
			boss.bullets.add(bullets.get(i).makeBullet(boss));
			boss.MP-=bullets.get(i).power/200;
		}
	}
	
}
