package ch.logixisland.anuto.game.theme;

import android.graphics.Color;

import ch.logixisland.anuto.R;

/**
 * Created by tom on 10/18/16.
 */

public class DarkTheme extends Theme {

    public DarkTheme() {
        foregroundColor = Color.WHITE;
        backgroundColor = Color.BLACK;

        textColor = Color.WHITE;
        textBackgroundColor = Color.BLACK;
    }



    //public static  int HEALTHBARBG = Color.WHITE;
    //public static  int HEALTHBARBD = Color.DKGRAY;

    public int resourceMap(int id) {
        switch (id) {
            case R.drawable.base1: id=R.drawable.base1_dk; break;
            case R.drawable.base2: id=R.drawable.base2_dk; break;
            case R.drawable.base3: id=R.drawable.base3_dk; break;
            case R.drawable.base4: id=R.drawable.base4_dk; break;
            case R.drawable.base5: id=R.drawable.base5_dk; break;
            case R.drawable.blob: id=R.drawable.blob_dk; break;
            case R.drawable.canon_dual: id=R.drawable.canon_dual_dk; break;
            case R.drawable.canon_mg: id=R.drawable.canon_mg_dk; break;
            case R.drawable.canon_mg_shot: id=R.drawable.canon_mg_shot_dk; break;
            case R.drawable.canon: id=R.drawable.canon_dk; break;
            case R.drawable.canon_shot: id=R.drawable.canon_shot_dk; break;
            case R.drawable.flyer: id=R.drawable.flyer_dk; break;
            case R.drawable.glue_effect: id=R.drawable.glue_effect_dk; break;
            case R.drawable.glue_gun: id=R.drawable.glue_gun_dk; break;
            case R.drawable.glue_shot: id=R.drawable.glue_shot_dk; break;
            case R.drawable.glue_tower_gun: id=R.drawable.glue_tower_gun_dk; break;
            case R.drawable.grenade: id=R.drawable.grenade_dk; break;
            case R.drawable.healer: id=R.drawable.healer_dk; break;
            case R.drawable.laser_tower1: id=R.drawable.laser_tower1_dk; break;
            case R.drawable.laser_tower2: id=R.drawable.laser_tower2_dk; break;
            case R.drawable.laser_tower3: id=R.drawable.laser_tower3_dk; break;
            case R.drawable.minelayer: id=R.drawable.minelayer_dk; break;
            case R.drawable.mine: id=R.drawable.mine_dk; break;
            case R.drawable.mortar: id=R.drawable.mortar_dk; break;
            case R.drawable.plateau1: id=R.drawable.plateau1_dk; break;
            case R.drawable.rocket_fire: id=R.drawable.rocket_fire_dk; break;
            case R.drawable.rocket_launcher: id=R.drawable.rocket_launcher_dk; break;
            case R.drawable.rocket: id=R.drawable.rocket_dk; break;
            case R.drawable.soldier: id=R.drawable.soldier_dk; break;
            case R.drawable.sprinter: id=R.drawable.sprinter_dk; break;
            case R.drawable.teleport_tower: id=R.drawable.teleport_tower_dk; break;

        }
        return id;
    }


}
