package de.htw.colorbattle.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;


/**
 * Class to serialize and deserialize objects before sending over network
 *
 */
public class SerializeUtils {

	
	/**
	 * Method to serialize object into byte code
	 * @param o Object for serialization
	 * @return Byte code of the serialized object
	 */
	public static byte[] serializeObject(Object o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(o);
			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();

			return buf;
		} catch (IOException ioe) {
			Gdx.app.error("Serialize: ", "can't serialize object.", ioe);
			return null;
		}
	}

	/**
	 * Method to deserialize byte code into objects
	 * @param b Byte code to deserialization
	 * @return Object of the deserialization
	 */
	public static Object deserializeObject(byte[] b) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(b));
			Object object = in.readObject();
			in.close();

			return object;
		} catch (ClassNotFoundException cnfe) {
			Gdx.app.error("Deserialize: ", "class not found error", cnfe);
			return null;
		} catch (IOException ioe) {
			Gdx.app.error("Deserialize: ", "can't deserialize object.", ioe);
			return null;
		}
	}

}
