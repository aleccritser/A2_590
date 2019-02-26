package app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ac.ArithmeticEncoder;
import io.OutputStreamBitSink;

public class ACEncodeVideoFile {

	public static void main(String[] args) throws IOException {
		String input_file_name = "/Users/aleccritser/Downloads/out.dat";
		String output_file_name = "/Users/aleccritser/Downloads/encodedVid.txt";

		int range_bit_width = 40;

		FileInputStream fis = new FileInputStream(input_file_name);

		int next_byte = fis.read();
		int[] symbol_counts = new int[256];

		int num_symbols = 0;

		Integer[] diffs = new Integer[512];
		int[] diffCount = new int[512];

		int refVal = next_byte;
		int check = 0;
		while (next_byte != -1) {
			symbol_counts[next_byte]++;

			diffCount[(next_byte - refVal) + 255]++;
			num_symbols++;
			refVal = next_byte;
			next_byte = fis.read();
			check++;
		}
		fis.close();

		int totPixels = 0;

		Integer[] symbols = new Integer[256];

		for (int i = 0; i < 256; i++) {
			symbols[i] = i;
			System.out.println(i + ", " + symbol_counts[i]);
			totPixels += symbol_counts[i];
		}

		for (int i = 0; i < 512; i++) {
			diffs[i] = i - 255;
			System.out.println(diffs[i] + ", " + diffCount[i]);
		}

		System.out.println(totPixels);

		FreqCountIntegerSymbolModel model = new FreqCountIntegerSymbolModel(diffs, diffCount);

		ArithmeticEncoder<Integer> encoder = new ArithmeticEncoder<Integer>(range_bit_width);

		FileOutputStream fos = new FileOutputStream(output_file_name);
		OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);

		for (int i = 0; i < 512; i++) {
			bit_sink.write(diffCount[i], 32);
		}

		fis = new FileInputStream(input_file_name);
		int fByte = fis.read();

		bit_sink.write(fByte, 8);
		bit_sink.write(num_symbols, 32);
		bit_sink.write(range_bit_width, 8);

		int count = 0;
		while (fis.available() > 0) {
			int nByte = fis.read();
			int writeDif = (fByte - nByte) * -1;
			encoder.encode(writeDif, model, bit_sink);

			count++;
			fByte = nByte;
		}

		fis.close();

		encoder.emitMiddle(bit_sink);
		bit_sink.padToWord();
		fos.close();
	}
}
