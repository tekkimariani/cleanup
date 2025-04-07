package com.tekkimariani.cleanup;

import org.tinylog.Logger;

import com.tekkimariani.cleanup.app.App;

public class Main {
	
    public static void main(String[] args) {
    	Logger.info("Cleanup v0.1");
    	new App();
    }

}