int	val;	/* Integer-Wert	*/
unsigned int x, y;
unsigned int width, height;	/* Breite und H�he des Bildes	*/
unsigned long py;		/* Zeiger auf erstes Element der aktuellen Zeile	*/
unsigned long pos;	/* Adresse des gew�nschten Elements	*/
:
:
for ( y = 0, py = 0; y < height; y++, py += width)  /* Schleife �ber alle Zeilen	*/
{
	/* py ist die Adresse des ersten Elementes in der aktuellen Zeile	*/
	for ( x = 0, pos = py; x < width; x++, pos++)  /* Schleife �ber alle Spalten	*/
	{
		/* pos wird mit Adresse des ersten Elementes initialisiert
		 * und dann nur inkremetiert, um von Spalte zu Spalte zu springen
		 */
		val = bild[pos]; /* Wert an der Position [y,x]	*/
		:
	}
}
: