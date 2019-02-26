package app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ac.ArithmeticDecoder;
import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

public class ACDecodeVideoFile {

	public static void main(String[] args) throws InsufficientBitsLeftException, IOException {
		String input_file_name = "/Users/aleccritser/Downloads/encodedVid.txt";
		String output_file_name = "/Users/aleccritser/Downloads/decompVid.txt";

		FileInputStream fis = new FileInputStream(input_file_name);

		InputStreamBitSource bit_source = new InputStreamBitSource(fis);

		// Read in symbol counts and set up model

		int[] symbol_counts = new int[512];
		Integer[] symbols = new Integer[512];

		for (int i = 0; i < 512; i++) {
			symbol_counts[i] = bit_source.next(32);
			symbols[i] = i;
		}

		int fByteVal = bit_source.next(8);

		FreqCountIntegerSymbolModel model = new FreqCountIntegerSymbolModel(symbols, symbol_counts);

		// Read in number of symbols encoded

		int num_symbols = bit_source.next(32);

		// Read in range bit width and setup the decoder

		int range_bit_width = bit_source.next(8);
		ArithmeticDecoder<Integer> decoder = new ArithmeticDecoder<Integer>(range_bit_width);

		int count = 1;

		// Decode and produce output.

		System.out.println("Uncompressing file: " + input_file_name);
		System.out.println("Output file: " + output_file_name);
		System.out.println("Range Register Bit Width: " + range_bit_width);
		System.out.println("Number of symbols: " + num_symbols);

		FileOutputStream fos = new FileOutputStream(output_file_name);

		for (int i = 0; i < num_symbols; i++) {
			fos.write(fByteVal);
			int diff = (255 - decoder.decode(model, bit_source)) * -1;

			fByteVal = fByteVal + diff;

		}

		System.out.println("Done.");
		fos.flush();
		fos.close();
		fis.close();
	}
}