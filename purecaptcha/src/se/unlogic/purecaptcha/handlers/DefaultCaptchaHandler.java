package se.unlogic.purecaptcha.handlers;

import java.awt.Color;
import java.util.ArrayList;

import se.unlogic.purecaptcha.Captcha;
import se.unlogic.purecaptcha.Config;
import se.unlogic.purecaptcha.Filter;
import se.unlogic.purecaptcha.SimpleConfig;
import se.unlogic.purecaptcha.filters.DefaultBackground;
import se.unlogic.purecaptcha.filters.DefaultNoise;
import se.unlogic.purecaptcha.filters.WaterRipple;
import se.unlogic.purecaptcha.textgenerators.DefaultTextGenerator;
import se.unlogic.purecaptcha.wordrenderers.DefaultWordRenderer;


public class DefaultCaptchaHandler extends CaptchaHandler {

	public DefaultCaptchaHandler(String sessionAttribute, long validationTimeout, boolean caseSensitive) {

		super(new Captcha(getDefaultConfig()), sessionAttribute, validationTimeout, caseSensitive);
	}

	private static Config getDefaultConfig() {

		SimpleConfig config = new SimpleConfig();
		
		config.setHeight(80);
		config.setWidth(250);
		
		config.setTextGenerator(new DefaultTextGenerator(5, DefaultTextGenerator.BIG_LETTERS));
		config.setWordRenderer(new DefaultWordRenderer(DefaultWordRenderer.getJavaDefaultFonts(50),Color.BLACK));
		
		ArrayList<Filter> filters = new ArrayList<Filter>();
		
		filters.add(new WaterRipple());
		filters.add(DefaultNoise.getDefaultNoiseTypeTwo(Color.GRAY));
		filters.add(new DefaultBackground(Color.LIGHT_GRAY,Color.WHITE));
		
		config.setFilters(filters);
		
		return config;
	}
}
