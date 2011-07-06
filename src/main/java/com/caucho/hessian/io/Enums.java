package com.caucho.hessian.io;

import java.lang.reflect.Method;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class Enums
{
    static Method isEnumMethod;
    static {
        try
        {
            isEnumMethod = Class.class.getMethod("isEnum", new Class[] {});
        }
        catch (NoSuchMethodException e)
        {
        }
    }

    static boolean isEnum(Class cls)
    {
        if (isEnumMethod != null)
        {
            try
            {
                return ((Boolean) isEnumMethod.invoke(cls, new Object[0])).booleanValue();
            }
            catch (Exception e)
            {
            }
        }

        return false;
    }
}
