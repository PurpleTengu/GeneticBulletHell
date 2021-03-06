package atouhougame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import atouhougame.bullets.Bullet;
import atouhougame.gamescreens.TouhouGame;
import atouhougame.patterncommands.AttackPattern;
import framework.BakedGameComponent;
import framework.FadeoutGameComponent;
import framework.Game;
import framework.GameComponent;
import framework.Global;
import framework.Group;
import framework.Keys;
import framework.Point;

public class Boss extends BakedGameComponent{
	
	protected ArrayList<AttackPattern> patterns = new ArrayList<AttackPattern>(0);
	public Group<Bullet> bullets = new Group<Bullet>();
	
	public int destX, destY;
	
	/**
	 * fireRate: Bullets per Second
	 * fireMillis: milliseconds since last bullet fired
	 * manaRegenRate: rate of mana regeneration in mana / second
	 */
	public int lastfired=0, HP, maxHP, maxMP, volleySize, currentPhase=0, totalPhases, symmetry;
	public long comMillis=0;
	public double MP, manaRegenRate, comRate, weight, moveSpeed,dashDist, power,bulletSpeed,torquePower;
	double radius;
	
	public boolean phaseChanged= false, active = false;
	public boolean lockedToPlayer = false;
	
	public double angle = 0, destAngle;
	
	public Color baseColor;
	
	public Boss(int x, int y, BossSeed seed){
		super(x,y,makeImage(seed),TouhouGame.playFieldLeft,TouhouGame.playFieldRight,GameComponent.BOUNDARY_BLOCK);
		
		this.baseColor 		=seed.color;
		this.power 			=seed.STR*0.5;
		this.bulletSpeed	=10+seed.DEX;
		this.patterns		=seed.patterns;
		this.weight			=(seed.STR/5.0+seed.CON)/2;
		this.maxHP			=(50+(int) seed.CON)*4;
		this.HP				=maxHP;
		this.maxMP			=(int) seed.WIS/2;
		this.MP				=maxMP/4.0;
		this.manaRegenRate	=seed.INT/500;
		this.moveSpeed		=5+10*seed.LUK/seed.CON;
		this.volleySize		=(int)(seed.INT*0.8+seed.STR*0.6+seed.DEX*0.4)/10;//most # of bullets boss can fire per volley
		
		this.comRate		=500+seed.CON/2+seed.STR/4-seed.INT-seed.DEX*2-seed.WIS;
		this.symmetry 		=3+(int) (seed.INT/15);
		
		destX=x;
		destY=y;

		
		this.radius			= seed.CON*0.25+10;
		this.size			= new Point(radius*2,radius*2);
		this.imageOffset 	= new Point(
				this.image.getWidth()/2-this.radius,
				this.image.getWidth()/2-this.radius
				);
		
	}
	
	/**
	 * updates the boss
	 * "shot" bullets are in fact stored to an internal list meant to be called by the game object the boss is a part of, then added to the component list of that game
	 */
	@Override
	public void update(long elapsedTime){
		
		if(this.active){
			
			//mana regen
			this.MP+=manaRegenRate*(1.0*elapsedTime/1000);
			if(MP>maxMP){MP=maxMP;}

			TouhouGame g = (TouhouGame) this.parentGame;
			//using Commands
			comMillis+=elapsedTime;
			if(comMillis>comRate){
				this.patterns.get(currentPhase).apply(this,g.player);
				comMillis=(long)(comMillis-comRate);
			}
			
			if(this.lockedToPlayer){
				this.destAngle=Global.findAngle(x, y, g.player.x, g.player.y);
			}
			
			//movement
			if(destX!=x || destY!=y){
				Point pawnt = Global.scaleAlong(moveSpeed*(elapsedTime/1000.0),Global.getsincos(x,y,destX,destY));
				
				//move angle as well
				if(angle!=destAngle){
					double diff = destAngle-angle;
					if(diff>Math.PI){
						destAngle=destAngle-2*Math.PI;
					}
					double ms = moveSpeed*elapsedTime/100000*Math.PI;
					if(destAngle-angle>0){
						angle+=ms;
					} else{
						angle-=ms;
					}
					angle=angle%(2*Math.PI);
					if(Math.abs(destAngle-angle)<ms){
						angle=destAngle;
					}
				}
				
				//if catches to avoid jittering
				if(Math.abs(x-destX)<pawnt.x){
					this.x=destX;
				}
				else{
					x+=pawnt.x;
				}
				if(Math.abs(y-destY)<pawnt.y){
					this.y=destY;
				}
				else{
					this.y+=pawnt.y;
				}
			}
		
			if(HP<=0 || Keys.isKeyPressed(KeyEvent.VK_T))
			{
				TGlobal.sound_explode_boss.play();
				if(currentPhase >= patterns.size()-1){
					this.kill();
				}
				else{
					HP=maxHP;
					currentPhase++;
					phaseChanged=true;
				}
			}
			
		}
		
		super.update(elapsedTime);
	}
	
	@Override
	public void kill(){
		if(this.visible && this.active){
			this.visible=false;
			this.active=false;
			TouhouGame g = (TouhouGame) this.parentGame;
			g.particles.addAll(Global.createSimpleExplosion(5,(int)this.size.x*4,this.color,
						this.getCenter(),this.velocity,2000,1000,(int)this.size.x, true));	
			g.particles.add(new Explosion(this.getCenter().x, this.getCenter().y,this.size.x*4,this.color));
			g.particles.add(new FadeoutGameComponent((int)(this.getCenter().x), (int)(this.getCenter().y), this.image, 0, 1000, GameComponent.BOUNDARY_KILL_ON_CROSS, true));
		}
		super.kill();
	}
	
	/**
	 * meant to be called by the Game object the boss is in. 
	 * @return all the bullets in that the boss has been storing
	 */
	public Group<Bullet> getBullets(boolean remove){
		Group<Bullet> bull = bullets.clone();
		if (remove){bullets.clear();}
		return bull;
	}
	
	@Override
	public void setParent(Game g){
		this.bullets.setParent(g);
		super.setParent(g);
	}
	
	@Override
	public Graphics render(Graphics g){
		super.render(g);
		g.setColor(Color.white);
		Point p = Global.rotate(new Point(0,this.radius),angle);
		g.drawLine((int)this.getCenter().x, (int)this.getCenter().y, (int)(this.getCenter().x+p.x), (int)(this.getCenter().y+p.y));
		return g;
	}
	
	public static BufferedImage makeImage(BossSeed seed){
		
		
		double radius = seed.CON*0.25+10;
		double numstep = 3+seed.LUK;
		double anglestep = 2*Math.PI/numstep;
		double spikeyness = Math.abs(radius * (seed.DEX/(BossSeed.nutrients/5)-seed.STR/(BossSeed.nutrients/5)));
		if(spikeyness<1){
			spikeyness=1;
		}
		
		BufferedImage img = new BufferedImage(2*(int)(radius+spikeyness),2*(int)(radius+spikeyness),BufferedImage.TYPE_INT_ARGB);
		int center = img.getWidth()/2;
		Graphics g = img.getGraphics();
		
		Polygon poly = new Polygon();
		
		Random r = new Random((long) (seed.CON+seed.DEX+seed.INT+seed.LUK+seed.STR+seed.WIS));
		
		double tempradius = radius;
		
		for(int i=0; i<numstep; i++){
			tempradius = radius+spikeyness*(r.nextDouble()-r.nextDouble());
			if(tempradius<radius/2){tempradius=radius/2;}
			
			Point p = Global.rotate(new Point(0, -tempradius),anglestep*i);
			
			poly.addPoint(center+(int)p.x, center+(int)p.y);
			
		}
		
		g.setColor(new Color(seed.color.getRed()+20, seed.color.getGreen()+20, seed.color.getBlue()+20));
		g.fillOval((int)spikeyness, (int)spikeyness, (int)(radius*2), (int)(radius*2));
		g.setColor(seed.color);
		g.fillPolygon(poly);
		
		return img;
	}
}
