# Laby labyrintti

Sovellus etsii mustavalkoisista labyrinttikuvista reitin lähdöstä maaliin valkoisia alueita pitkin.

<img src="/documentation/aloitusnakyma.png" height=600 title="Aloitusnäkymä">


### Käynnistäminen linux-ympäristössä

Lataa .jar-tiedosto [täältä](https://github.com/Skorp7/Laby/releases/tag/v1)

Lataa kansio images [täältä](https://github.com/Skorp7/Laby/tree/master/images)

.jar-tiedoston tulee olla samassa kansiossa kuin kansio *images*.

Käynnistä sovellus komentoriviltä ```java -jar tiedostonimi.jar```

### Käyttöohjeet

Voit ladata haluamasi labyrinttikuvan antamalla tiedoston suhteellisen polun kenttän ja klikkaamalla *lataa kuva*. Bitmap toimii parhaiten.

Lisää lähtöpaikka ja maali klikkaamalla ja etsi reitti. 

Jos kuva ei ole täysin mustavalkoinen, vaan harmaasävyinen, tai on esim. huonosti skannattu, voit muuttaa mustien labyrinttiseinämien "vahvuutta" 
liukurilla. Jos vahvuutta vähennetään, reitti voi silloin kulkea myös harmahtavien alueiden läpi. Oletuksena reitti kulkee vain valkoisella alueella.

Jos kuvan labyrinttiseinämissä on reikiä huonosta skannauksesta tms. johtuen, voit piirtää kuvaan lisää mustaa seinää toiminnolla *lisää seinää*.

<img src="/documentation/labyrintti.png" height=600 title="Ratkaistu labyrintti">
