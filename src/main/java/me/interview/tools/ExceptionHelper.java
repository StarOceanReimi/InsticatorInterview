package me.interview.tools;

import java.util.function.Supplier;

public abstract class ExceptionHelper {

	public static Supplier<RuntimeException> customMessage(String message, Object...args) {
		return ()->new RuntimeException(String.format(message, args));
	}
	
}
