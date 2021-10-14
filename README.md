# Engagement Reputation Adjustment

A tiny mini-mod that adjusts player reputation behind the scenes based on won engagements. If you have beaten an enemy
fleet, and there are any factions that are hostile to the faction that you have just beaten, this mod will provide
a small increase to your reputation with that faction. Terms and conditions apply.

## Numbers

Reputation gain can vary between 0 and 0.01 depending on how hostile two factions are. It is given per engagement,
regardless of engagement size. This equals to, at most, 1 reputation point as displayed in-game.

## Exclusion list

Does not increase player rep with:

* Lost battles (technically their enemies could hate you more for failing)
* Beaten faction (you already got the rep hit from them)
* Commissioned faction (rep adjustments governed by vanilla)
* Blacklisted factions (see `EraListener.java` in `src/`)
