/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gcewing.sg.gui.malisis.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Minimal stub — only the reflective access methods used by the vendored code.
 */
public class AsmUtils
{
	public static Field changeFieldAccess(Field field)
	{
		if (field != null)
			field.setAccessible(true);
		return field;
	}

	public static Method changeMethodAccess(Method method)
	{
		if (method != null)
			method.setAccessible(true);
		return method;
	}

	public static Field changeFieldAccess(Class<?> clazz, String fieldName)
	{
		return changeFieldAccess(clazz, fieldName, fieldName);
	}

	public static Field changeFieldAccess(Class<?> clazz, String fieldName, String srgFieldName)
	{
		try
		{
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		}
		catch (NoSuchFieldException e)
		{
			try
			{
				Field field = clazz.getDeclaredField(srgFieldName);
				field.setAccessible(true);
				return field;
			}
			catch (NoSuchFieldException e2)
			{
				return null;
			}
		}
	}

	public static Field changeFieldAccess(Class<?> clazz, String fieldName, String srgFieldName, boolean forceAccess)
	{
		return changeFieldAccess(clazz, fieldName, srgFieldName);
	}

	public static Method changeMethodAccess(Class<?> clazz, String methodName, String srgMethodName, Class<?>... params)
	{
		try
		{
			Method method = clazz.getDeclaredMethod(methodName, params);
			method.setAccessible(true);
			return method;
		}
		catch (NoSuchMethodException e)
		{
			try
			{
				Method method = clazz.getDeclaredMethod(srgMethodName, params);
				method.setAccessible(true);
				return method;
			}
			catch (NoSuchMethodException e2)
			{
				return null;
			}
		}
	}
}
