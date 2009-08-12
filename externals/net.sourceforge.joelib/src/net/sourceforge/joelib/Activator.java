/*******************************************************************************
 * Copyright (c) 2008 Egon Willighagen
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.sourceforge.joelib;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

    public static final String PLUGIN_ID = "net.sourceforge.joelib";

    private static Activator sharedInstance;

    public Activator() {}

    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    public void stop(BundleContext context) throws Exception {
        sharedInstance = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return sharedInstance;
    }

}
