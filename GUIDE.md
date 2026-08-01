## mod usage guide
# WIP

## before coding: 
- download the octlib lastest release
- add to your mod.hjson/json this:
  `dependencies: [octlib]`

## how to use:

### QuantumReactor:

`type: QuantumReactor` just ImpactReactor with custom explosion

`explosionRadius` radius of explosion in world units(8 = 1 block)

`explosionDamage` final explosion damage

`attractStrength` strengh of attraction to blackhole center

`maxPullRadius` radius of attraction

`anomalyLifetime` lifetime of blackhole(60 = 1 sec)


`coreColor` color of blackhole core

`energyColor` another color

`plasmaColor` and yet another color


`anomalyDespawnEffect` final explosion effect

`anomalyUpdateEffect` effect played in cycle all lifetime

`anomalyMiniExplodeEffect` mini explosion effect

`shockwaveHitEffect` shockwave effect


`miniExplodeInterval` interval of mini explosions

`updateVfxInterval` interval of `anomalyUpdateEffect`

`shockwaveCount` how much shockwaves on final boom

`shockwaveDamage` shockwave damage


`collapseAnomaly` custom bullet spawned on explosion

## WARNING: if you override `collapseAnomaly` other params WILL BE IGNORED






### TempGenerator: 

`type: TempGenerator` a generator that outputs power and slowly decharges

`startingGeneration` starting power generation

`decreaseAmount` amout by this decresaed generation

