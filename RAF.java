import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

public class RAF {

	public static void main(String[] args) {
		
		/*
		 * VAMOS A EMPEZAR CON ALGO IMPORTANTE, DEFINIENDO LOS TAMAÑOS (EN BYTES) DE LAS VARIABLES
		 * Y COMO NOS AFECTARA EN NUESTRA "LINEA"
		 * 
		 */

		//Nos marca cuantos bits tiene un Byte.
		int BITBYTES = Byte.SIZE;
		//siempre tendrá 1 byte
		int BOOLEANBYTES = 1;
		//Nos devuelve la cantidad de bits de un int, pero necesitamos los Bytes (dividimos)
		int INTBYTES = Integer.SIZE/BITBYTES;
		//Nos devuelve la cantidad de bits de un char, pero necesitamos los Bytes (dividimos)
		int CHARBYTES = Character.SIZE/BITBYTES;

		int id = 0;

		ArrayList<String> cosas = new ArrayList<String>();
		cosas.add("Pedrolo 5");
		cosas.add("Espada 3");
		cosas.add("Arco 2");
		cosas.add("Daga 1");
		cosas.add("Pluma 0");


		/*Vamos a componer las lineas a partir de:
		 *un booleano que marca si el objeto está en el inventario
		 *un int que marca el id,
		 *un "campo" de texto formado por 15 chars, para un nombre de objeto
		 *otro int para el peso del objeto
		 *
		 *de esta forma podemos calcular cual es el tamaño total de nuestra linea así:
		 */

		//Este es el tamaño total de la "linea"
		int TAMAÑODELINEA = BOOLEANBYTES + INTBYTES + CHARBYTES*15 + INTBYTES;

		System.out.println("Un booleano ocupa: " + BOOLEANBYTES + ", un Integer(int) ocupa: " 
				+ INTBYTES + ", un Character(char) ocupa: " + CHARBYTES + 
				"\nEn total nuestra lina ocupará: " + TAMAÑODELINEA);

		//GENERAMOS LOS DOS FICHEROS CON LOS QUE TRABAJAR
		File inventario = new File("inventario.txt");
		File mochila = new File("mochila.txt");

		try {
			
			
			/*
			 * AL CREAR EL RAF LE DESIGNAMOS EL ARCHIVO SOBRE EL QUE TRABAJA Y LOS PERMISOS SOBRE
			 * SOBRE ÉL, EN ESTE CASO AMBOS SERÁN DE LECTURA/ESCRITURA: "rw"
			 */
			
			RandomAccessFile rafInventario = new RandomAccessFile(inventario, "rw");
			RandomAccessFile rafMochila = new RandomAccessFile(mochila, "rw");

			/*
			 *INTRODUCIMOS TODOS LOS DATOS EN NUETRO INVENTARIO, PARA ELLO USAMOS LA CLASE 
			 *rafInventario, y vamos pidiendole que escriba con un "for" todo lo que nos interesa
			 *
			 */

			for (int itin = 0 ;itin < cosas.size(); itin++){
				//primero el booleano
				rafInventario.writeBoolean(true);
				//seguimos con su id y sumamos uno, para el siguiente
				rafInventario.writeInt(id);
				id++;
				//el nombre. RAF permite el metodo writeChars (con s) que guarda una cadena 
				//completa, sin embargo solo escribirá de forma completa la palabra, 
				//y nosotros queremos controlar el tamaño a 15 caracteres, por lo que de nuevo
				//necesitamos realizar algún tipo de padding
				String nombrePadding = cosas.get(itin).split(" ")[0];
				while (nombrePadding.length() < 15) nombrePadding+="#";
				rafInventario.writeChars(nombrePadding);
				//por ultimo el peso, esta vez será un int
				rafInventario.writeInt(Integer.parseInt(cosas.get(itin).split(" ")[1]));
			}

			/*
			 * Si todo ha salido bien deberiamos tener un archivo con esos datos, 
			 * pero por si acaso siempre podemos pedirle que nos imprima todo con todo bucle.
			 * Situamos el cursos en el principio del documento con seek.
			 */

			rafInventario.seek(0);
			
			System.out.println("\nInventario: ");
			
			while (rafInventario.length() > rafInventario.getFilePointer()){
				boolean boleano = rafInventario.readBoolean();
				id = rafInventario.readInt();
				/*
				 * Aquí nos encontramos un problema y es que RAF no tiene un metodo para devolver
				 * cadenas de caracteres, por lo que tenemos que sacarlos caracter por caracter,
				 * de nuevo usando un bucle, ya sabiendo que tenemos que encontrar 15 caracteres,
				 * es facil
				 */
				String nombre = "";
				for (int chars = 0; chars < 15; chars++){
					nombre += rafInventario.readChar();
				}

				//Podemos afinar más si le quitamos el padding para que se muestre mejor.
				nombre = nombre.split("#")[0];
				
				//Ahora el peso
				int peso = rafInventario.readInt();

				System.out.println(boleano + " id: "
						+ id + ", nombre: " + nombre + ", peso: " + peso);
			}
			
			/*
			 *AHORA TOCA ALGO QUE PODRIA PARECER DIFICIL, PERO NO LO ES, COPIAREMOS TODO EL
			 *INVENTARIO DENTRO DE LA MOCHILA.
			 *
			 *AQUNUE PODRÍAMOS IR COMO ANTES AL IMPRIMIR, ES MUCHO MÁS SENCILLO USAR LA 
			 *FUNCIONALIDAD QUE COPIA DIRECTAMETE BYTES
			 *
			 *EMPEZEMOS COLOCANDONOS DE NUEVO EN EL PRINCIPIO DEL INVENTARIO
			 *
			 */
			
			rafInventario.seek(0);
			
			//Ahora con un simple while copiaremos y pegaremos.
			
			while (rafInventario.length() > rafInventario.getFilePointer()){
				rafMochila.writeByte(rafInventario.readByte());
			}
			
			/*
			 * tan sencillo como eso, ahora podemos comprobar que
			 * fectivamente son iguales leyendo igual que lo hicimos anteriormente
			 * 
			 * ES IMPORTANTE DEVOLVER EL CURSOR A 0 YA QUE TRAS LA ESCRITURA SE ENCUENTRA AL FINAL
			 * 
			 */
			rafMochila.seek(0);
			System.out.println("\nMochila: ");
			
			while (rafMochila.length() > rafMochila.getFilePointer()){
				
				boolean boleano = rafMochila.readBoolean();
				id = rafMochila.readInt();
		
				String nombre = "";
				for (int chars = 0; chars < 15; chars++){
					nombre += rafMochila.readChar();
				}
				nombre = nombre.split("#")[0];
				
				int peso = rafMochila.readInt();

				System.out.println(boleano + " id: "
						+ id + ", nombre: " + nombre + ", peso: " + peso);
			}
			
			
			/*AHORA VAMOS A INTENTAR IMPRIMIR UN OBJETO NADA MAS, PUESTO QUE SABEMOS 
			 * CUAL ES EL TAMAÑO EN BYTES DE NUESTRA LINEA (QUE HEMOS CALCULADO Y GUARDADO
			 * EN UNA VARIABLE: TAMAÑODELINEA, PODEMOS SALTAR (EN CASO DE QUE SUPIERAMOS EL ID
			 * O LA POSICION DE LA LINEA) A CUALQUIER LUGAR TAN SENCILLO COMO:
			 * 
			 * queremos el objeto 3, el arco, que está en la tercera linea, tenemos que
			 * situar el "cursor" en esa posicion con seek. IMPORTANTE QUE LA LINEA ES 3
			 * PERO EN REALIDAD EMPEZARIAMOS A CONTAR DESDE 0.
			 */
			
			//Multiplicamos por el tamaño total de la linea para situarnos en el byte adecuado
			// restamos 1 a la linea a la que queremos ir por que se empieza a contar desde el 0
			rafMochila.seek(TAMAÑODELINEA * (3-1));
			
			//imprimimos solo esa linea:
			
			boolean boleano = rafMochila.readBoolean();
			id = rafMochila.readInt();
	
			String nombre = "";
			for (int chars = 0; chars < 15; chars++){
				nombre += rafMochila.readChar();
			}
			nombre = nombre.split("#")[0];

			int peso = rafMochila.readInt();

			System.out.println("\nLinea 3: " + boleano + " id: "
					+ id + ", nombre: " + nombre + ", peso: " + peso);


			/*
			 * Toca algo un poco "más dificil" cambiar un solo elemento de la lista
			 * puesto que sabemos el orden de los datos y cuanto ocupan gracias a nuestras
			 * constantes solo tenemos que usar el metodo seek para colocarnos en el inicio
			 * del campo a cambiar y pedirle que escriba el nuevo dato.
			 * 
			 * Vamos a cambiar la pluma por Plomo, y su peso por 10
			 * 
			 */

			/*
			 * La pluma es el 5 elemento de la lista, nos situamos en la linea y ahora, le sumamos
			 * le sumamos bytes, hasta llegar al nombre, necesita el tamaño del booleano y el del
			 * primer int
			 */

			rafMochila.seek((TAMAÑODELINEA * (5-1)) + BOOLEANBYTES + INTBYTES);
			rafMochila.writeChars("Plomo");

			/*
			 * Hacemos los mismo con la pluma, y como no estamos seguros de donde ha pidido
			 *acabar el "cursor" reusamos seek igual.
			 */
			
			//esta vez le sumamos CHARBYTES*15 ya que son los que le asignamos al principio
			rafMochila.seek((TAMAÑODELINEA * (5-1)) + BOOLEANBYTES + INTBYTES + CHARBYTES*15);
			rafMochila.writeInt(10);
			
			//vamos a imprimir la linea para vez que es cierto
			System.out.println("\nCambio de objeto: ");
			rafMochila.seek(0);
	
			while (rafMochila.length() > rafMochila.getFilePointer()){
				
				boleano = rafMochila.readBoolean();
				id = rafMochila.readInt();
		
				nombre = "";
				for (int chars = 0; chars < 15; chars++){
					nombre += rafMochila.readChar();
				}
				nombre = nombre.split("#")[0];
				
				peso = rafMochila.readInt();

				System.out.println(boleano + " id: "
						+ id + ", nombre: " + nombre + ", peso: " + peso);
			}

			
			/*OTROS ELEMENTOS IMPORTANTES QUE PODRIA TENER NUESTRA CLASE SERÍA PARA AÑADIR
			 * NUEVOS ELEMENTOS, NOS COLOCARIAMOS AL FINAL DE NUESTRO ARCHIVO CON UN
			 * SEEK(RAF.LENGTH) PERO... ¿QUE ID LE DAMOS?
			 * 
			 * ES SENCILLO, UNA VEZ SITUADOS AL FINAL DEL DOCUMENTO LE RESTAMOS UNA LINEA COMPLETA,
			 * ASÍ, AUNQUE NO SEPAMOS CUANTAS LINEAS TIENE, SIEMPRE NOS SITUAREMOS AL COMIENZO
			 * DEL ULTIMO ELEMENTO, DESPUES LE PEDIMOS QUE AVANCE SOBRE EL BOOLEANO,
			 * LEYENDOLO POR EJEMPLO, Y DE NUEVO LEEMOS EL ID Y SE LO ASIGNAMOS A LA VARIABLE INT ID
			 * (QUE PARA ESO SE CREÓ AL PRINCIPIO DEL DOCUMENTO)
			 * 
			 * AHORA NOS BASTA CON VOLVER A SITUARNOS AL FINAL E INTRODUCIR EL NUEVO ELEMENTO
			 * ASIGNANDOLE LA ID OBTENIDA +1,
			 * 
			 * EL USO DE TRUE O FALSE NO SOLO ES "EDUCATIVO" NOS SIRVE PARA, POR EJEMPLO, "BORRAR"
			 * ELEMENTOS, SI CAMBIAMOS EL TRUE POR EL FALSE Y LE DECIMOS, POR EJEMPLO, QUE NO NOS
			 * MUESTRE LOS ELEMENTOS SI EMPIEZAN CON UN FALSE... EL RESTO YA ES TU IMAGINACIÓN :p
			 */
			

		} catch (IOException e) {
			e.printStackTrace();
		}




	}

}
