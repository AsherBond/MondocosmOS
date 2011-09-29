

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;


public class GColorBrewerColorSchemeSet
         extends
            GColorSchemeSet {


   public static final GColorBrewerColorSchemeSet INSTANCE = new GColorBrewerColorSchemeSet();


   private GColorBrewerColorSchemeSet() {
      super("ColorBrewer");

      initialize();
   }


   private void initialize() {


      // ColorScheme "Accent"  Type=Qualitative
      add(new GColorScheme("Accent", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.7882353f, 0.49803922f),
                        GColorF.newRGB(0.99215686f, 0.7529412f, 0.5254902f)
      }));
      add(new GColorScheme("Accent", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.7882353f, 0.49803922f),
                        GColorF.newRGB(0.74509805f, 0.68235296f, 0.83137256f),
                        GColorF.newRGB(0.99215686f, 0.7529412f, 0.5254902f)
      }));
      add(new GColorScheme("Accent", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.7882353f, 0.49803922f),
                        GColorF.newRGB(0.74509805f, 0.68235296f, 0.83137256f),
                        GColorF.newRGB(0.99215686f, 0.7529412f, 0.5254902f),
                        GColorF.newRGB(1.0f, 1.0f, 0.6f)
      }));
      add(new GColorScheme("Accent", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.7882353f, 0.49803922f),
                        GColorF.newRGB(0.74509805f, 0.68235296f, 0.83137256f),
                        GColorF.newRGB(0.99215686f, 0.7529412f, 0.5254902f),
                        GColorF.newRGB(1.0f, 1.0f, 0.6f),
                        GColorF.newRGB(0.21960784f, 0.42352942f, 0.6901961f)
      }));
      add(new GColorScheme("Accent", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.7882353f, 0.49803922f),
                        GColorF.newRGB(0.74509805f, 0.68235296f, 0.83137256f),
                        GColorF.newRGB(0.99215686f, 0.7529412f, 0.5254902f),
                        GColorF.newRGB(1.0f, 1.0f, 0.6f),
                        GColorF.newRGB(0.21960784f, 0.42352942f, 0.6901961f),
                        GColorF.newRGB(0.9411765f, 0.007843138f, 0.49803922f)
      }));
      add(new GColorScheme("Accent", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.7882353f, 0.49803922f),
                        GColorF.newRGB(0.74509805f, 0.68235296f, 0.83137256f),
                        GColorF.newRGB(0.99215686f, 0.7529412f, 0.5254902f),
                        GColorF.newRGB(1.0f, 1.0f, 0.6f),
                        GColorF.newRGB(0.21960784f, 0.42352942f, 0.6901961f),
                        GColorF.newRGB(0.9411765f, 0.007843138f, 0.49803922f),
                        GColorF.newRGB(0.7490196f, 0.35686275f, 0.09019608f)
      }));
      add(new GColorScheme("Accent", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.7882353f, 0.49803922f),
                        GColorF.newRGB(0.74509805f, 0.68235296f, 0.83137256f),
                        GColorF.newRGB(0.99215686f, 0.7529412f, 0.5254902f),
                        GColorF.newRGB(1.0f, 1.0f, 0.6f),
                        GColorF.newRGB(0.21960784f, 0.42352942f, 0.6901961f),
                        GColorF.newRGB(0.9411765f, 0.007843138f, 0.49803922f),
                        GColorF.newRGB(0.7490196f, 0.35686275f, 0.09019608f),
                        GColorF.newRGB(0.4f, 0.4f, 0.4f)
      }));


      // ColorScheme "Blues"  Type=Sequential
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.87058824f, 0.92156863f, 0.96862745f),
                        GColorF.newRGB(0.19215687f, 0.50980395f, 0.7411765f)
      }));
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.87058824f, 0.92156863f, 0.96862745f),
                        GColorF.newRGB(0.61960787f, 0.7921569f, 0.88235295f),
                        GColorF.newRGB(0.19215687f, 0.50980395f, 0.7411765f)
      }));
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.9529412f, 1.0f),
                        GColorF.newRGB(0.7411765f, 0.84313726f, 0.90588236f),
                        GColorF.newRGB(0.41960785f, 0.68235296f, 0.8392157f),
                        GColorF.newRGB(0.12941177f, 0.44313726f, 0.70980394f)
      }));
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.9529412f, 1.0f),
                        GColorF.newRGB(0.7411765f, 0.84313726f, 0.90588236f),
                        GColorF.newRGB(0.41960785f, 0.68235296f, 0.8392157f),
                        GColorF.newRGB(0.19215687f, 0.50980395f, 0.7411765f),
                        GColorF.newRGB(0.03137255f, 0.31764707f, 0.6117647f)
      }));
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.9529412f, 1.0f),
                        GColorF.newRGB(0.7764706f, 0.85882354f, 0.9372549f),
                        GColorF.newRGB(0.61960787f, 0.7921569f, 0.88235295f),
                        GColorF.newRGB(0.41960785f, 0.68235296f, 0.8392157f),
                        GColorF.newRGB(0.19215687f, 0.50980395f, 0.7411765f),
                        GColorF.newRGB(0.03137255f, 0.31764707f, 0.6117647f)
      }));
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.9529412f, 1.0f),
                        GColorF.newRGB(0.7764706f, 0.85882354f, 0.9372549f),
                        GColorF.newRGB(0.61960787f, 0.7921569f, 0.88235295f),
                        GColorF.newRGB(0.41960785f, 0.68235296f, 0.8392157f),
                        GColorF.newRGB(0.25882354f, 0.57254905f, 0.7764706f),
                        GColorF.newRGB(0.12941177f, 0.44313726f, 0.70980394f),
                        GColorF.newRGB(0.03137255f, 0.27058825f, 0.5803922f)
      }));
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9843137f, 1.0f),
                        GColorF.newRGB(0.87058824f, 0.92156863f, 0.96862745f),
                        GColorF.newRGB(0.7764706f, 0.85882354f, 0.9372549f),
                        GColorF.newRGB(0.61960787f, 0.7921569f, 0.88235295f),
                        GColorF.newRGB(0.41960785f, 0.68235296f, 0.8392157f),
                        GColorF.newRGB(0.25882354f, 0.57254905f, 0.7764706f),
                        GColorF.newRGB(0.12941177f, 0.44313726f, 0.70980394f),
                        GColorF.newRGB(0.03137255f, 0.27058825f, 0.5803922f)
      }));
      add(new GColorScheme("Blues", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9843137f, 1.0f),
                        GColorF.newRGB(0.87058824f, 0.92156863f, 0.96862745f),
                        GColorF.newRGB(0.7764706f, 0.85882354f, 0.9372549f),
                        GColorF.newRGB(0.61960787f, 0.7921569f, 0.88235295f),
                        GColorF.newRGB(0.41960785f, 0.68235296f, 0.8392157f),
                        GColorF.newRGB(0.25882354f, 0.57254905f, 0.7764706f),
                        GColorF.newRGB(0.12941177f, 0.44313726f, 0.70980394f),
                        GColorF.newRGB(0.03137255f, 0.31764707f, 0.6117647f),
                        GColorF.newRGB(0.03137255f, 0.1882353f, 0.41960785f)
      }));


      // ColorScheme "BrBG"  Type=Diverging
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84705883f, 0.7019608f, 0.39607844f),
                        GColorF.newRGB(0.3529412f, 0.7058824f, 0.6745098f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84705883f, 0.7019608f, 0.39607844f),
                        GColorF.newRGB(0.9607843f, 0.9607843f, 0.9607843f),
                        GColorF.newRGB(0.3529412f, 0.7058824f, 0.6745098f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.38039216f, 0.101960786f),
                        GColorF.newRGB(0.8745098f, 0.7607843f, 0.49019608f),
                        GColorF.newRGB(0.5019608f, 0.8039216f, 0.75686276f),
                        GColorF.newRGB(0.003921569f, 0.52156866f, 0.44313726f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.38039216f, 0.101960786f),
                        GColorF.newRGB(0.8745098f, 0.7607843f, 0.49019608f),
                        GColorF.newRGB(0.9607843f, 0.9607843f, 0.9607843f),
                        GColorF.newRGB(0.5019608f, 0.8039216f, 0.75686276f),
                        GColorF.newRGB(0.003921569f, 0.52156866f, 0.44313726f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.54901963f, 0.31764707f, 0.039215688f),
                        GColorF.newRGB(0.84705883f, 0.7019608f, 0.39607844f),
                        GColorF.newRGB(0.9647059f, 0.9098039f, 0.7647059f),
                        GColorF.newRGB(0.78039217f, 0.91764706f, 0.8980392f),
                        GColorF.newRGB(0.3529412f, 0.7058824f, 0.6745098f),
                        GColorF.newRGB(0.003921569f, 0.4f, 0.36862746f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.54901963f, 0.31764707f, 0.039215688f),
                        GColorF.newRGB(0.84705883f, 0.7019608f, 0.39607844f),
                        GColorF.newRGB(0.9647059f, 0.9098039f, 0.7647059f),
                        GColorF.newRGB(0.9607843f, 0.9607843f, 0.9607843f),
                        GColorF.newRGB(0.78039217f, 0.91764706f, 0.8980392f),
                        GColorF.newRGB(0.3529412f, 0.7058824f, 0.6745098f),
                        GColorF.newRGB(0.003921569f, 0.4f, 0.36862746f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.54901963f, 0.31764707f, 0.039215688f),
                        GColorF.newRGB(0.7490196f, 0.5058824f, 0.1764706f),
                        GColorF.newRGB(0.8745098f, 0.7607843f, 0.49019608f),
                        GColorF.newRGB(0.9647059f, 0.9098039f, 0.7647059f),
                        GColorF.newRGB(0.78039217f, 0.91764706f, 0.8980392f),
                        GColorF.newRGB(0.5019608f, 0.8039216f, 0.75686276f),
                        GColorF.newRGB(0.20784314f, 0.5921569f, 0.56078434f),
                        GColorF.newRGB(0.003921569f, 0.4f, 0.36862746f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.54901963f, 0.31764707f, 0.039215688f),
                        GColorF.newRGB(0.7490196f, 0.5058824f, 0.1764706f),
                        GColorF.newRGB(0.8745098f, 0.7607843f, 0.49019608f),
                        GColorF.newRGB(0.9647059f, 0.9098039f, 0.7647059f),
                        GColorF.newRGB(0.9607843f, 0.9607843f, 0.9607843f),
                        GColorF.newRGB(0.78039217f, 0.91764706f, 0.8980392f),
                        GColorF.newRGB(0.5019608f, 0.8039216f, 0.75686276f),
                        GColorF.newRGB(0.20784314f, 0.5921569f, 0.56078434f),
                        GColorF.newRGB(0.003921569f, 0.4f, 0.36862746f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.32941177f, 0.1882353f, 0.019607844f),
                        GColorF.newRGB(0.54901963f, 0.31764707f, 0.039215688f),
                        GColorF.newRGB(0.7490196f, 0.5058824f, 0.1764706f),
                        GColorF.newRGB(0.8745098f, 0.7607843f, 0.49019608f),
                        GColorF.newRGB(0.9647059f, 0.9098039f, 0.7647059f),
                        GColorF.newRGB(0.78039217f, 0.91764706f, 0.8980392f),
                        GColorF.newRGB(0.5019608f, 0.8039216f, 0.75686276f),
                        GColorF.newRGB(0.20784314f, 0.5921569f, 0.56078434f),
                        GColorF.newRGB(0.003921569f, 0.4f, 0.36862746f),
                        GColorF.newRGB(0.0f, 0.23529412f, 0.1882353f)
      }));
      add(new GColorScheme("BrBG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.32941177f, 0.1882353f, 0.019607844f),
                        GColorF.newRGB(0.54901963f, 0.31764707f, 0.039215688f),
                        GColorF.newRGB(0.7490196f, 0.5058824f, 0.1764706f),
                        GColorF.newRGB(0.8745098f, 0.7607843f, 0.49019608f),
                        GColorF.newRGB(0.9647059f, 0.9098039f, 0.7647059f),
                        GColorF.newRGB(0.9607843f, 0.9607843f, 0.9607843f),
                        GColorF.newRGB(0.78039217f, 0.91764706f, 0.8980392f),
                        GColorF.newRGB(0.5019608f, 0.8039216f, 0.75686276f),
                        GColorF.newRGB(0.20784314f, 0.5921569f, 0.56078434f),
                        GColorF.newRGB(0.003921569f, 0.4f, 0.36862746f),
                        GColorF.newRGB(0.0f, 0.23529412f, 0.1882353f)
      }));


      // ColorScheme "BuGn"  Type=Sequential
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.9764706f),
                        GColorF.newRGB(0.17254902f, 0.63529414f, 0.37254903f)
      }));
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.9764706f),
                        GColorF.newRGB(0.6f, 0.84705883f, 0.7882353f),
                        GColorF.newRGB(0.17254902f, 0.63529414f, 0.37254903f)
      }));
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.69803923f, 0.8862745f, 0.8862745f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.6431373f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f)
      }));
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.69803923f, 0.8862745f, 0.8862745f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.6431373f),
                        GColorF.newRGB(0.17254902f, 0.63529414f, 0.37254903f),
                        GColorF.newRGB(0.0f, 0.42745098f, 0.17254902f)
      }));
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.8f, 0.9254902f, 0.9019608f),
                        GColorF.newRGB(0.6f, 0.84705883f, 0.7882353f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.6431373f),
                        GColorF.newRGB(0.17254902f, 0.63529414f, 0.37254903f),
                        GColorF.newRGB(0.0f, 0.42745098f, 0.17254902f)
      }));
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.8f, 0.9254902f, 0.9019608f),
                        GColorF.newRGB(0.6f, 0.84705883f, 0.7882353f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.6431373f),
                        GColorF.newRGB(0.25490198f, 0.68235296f, 0.4627451f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f),
                        GColorF.newRGB(0.0f, 0.34509805f, 0.14117648f)
      }));
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.99215686f),
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.9764706f),
                        GColorF.newRGB(0.8f, 0.9254902f, 0.9019608f),
                        GColorF.newRGB(0.6f, 0.84705883f, 0.7882353f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.6431373f),
                        GColorF.newRGB(0.25490198f, 0.68235296f, 0.4627451f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f),
                        GColorF.newRGB(0.0f, 0.34509805f, 0.14117648f)
      }));
      add(new GColorScheme("BuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.99215686f),
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.9764706f),
                        GColorF.newRGB(0.8f, 0.9254902f, 0.9019608f),
                        GColorF.newRGB(0.6f, 0.84705883f, 0.7882353f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.6431373f),
                        GColorF.newRGB(0.25490198f, 0.68235296f, 0.4627451f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f),
                        GColorF.newRGB(0.0f, 0.42745098f, 0.17254902f),
                        GColorF.newRGB(0.0f, 0.26666668f, 0.105882354f)
      }));


      // ColorScheme "BuPu"  Type=Sequential
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8784314f, 0.9254902f, 0.95686275f),
                        GColorF.newRGB(0.53333336f, 0.3372549f, 0.654902f)
      }));
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8784314f, 0.9254902f, 0.95686275f),
                        GColorF.newRGB(0.61960787f, 0.7372549f, 0.85490197f),
                        GColorF.newRGB(0.53333336f, 0.3372549f, 0.654902f)
      }));
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.54901963f, 0.5882353f, 0.7764706f),
                        GColorF.newRGB(0.53333336f, 0.25490198f, 0.6156863f)
      }));
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.54901963f, 0.5882353f, 0.7764706f),
                        GColorF.newRGB(0.53333336f, 0.3372549f, 0.654902f),
                        GColorF.newRGB(0.5058824f, 0.05882353f, 0.4862745f)
      }));
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.7490196f, 0.827451f, 0.9019608f),
                        GColorF.newRGB(0.61960787f, 0.7372549f, 0.85490197f),
                        GColorF.newRGB(0.54901963f, 0.5882353f, 0.7764706f),
                        GColorF.newRGB(0.53333336f, 0.3372549f, 0.654902f),
                        GColorF.newRGB(0.5058824f, 0.05882353f, 0.4862745f)
      }));
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9843137f),
                        GColorF.newRGB(0.7490196f, 0.827451f, 0.9019608f),
                        GColorF.newRGB(0.61960787f, 0.7372549f, 0.85490197f),
                        GColorF.newRGB(0.54901963f, 0.5882353f, 0.7764706f),
                        GColorF.newRGB(0.54901963f, 0.41960785f, 0.69411767f),
                        GColorF.newRGB(0.53333336f, 0.25490198f, 0.6156863f),
                        GColorF.newRGB(0.43137255f, 0.003921569f, 0.41960785f)
      }));
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.99215686f),
                        GColorF.newRGB(0.8784314f, 0.9254902f, 0.95686275f),
                        GColorF.newRGB(0.7490196f, 0.827451f, 0.9019608f),
                        GColorF.newRGB(0.61960787f, 0.7372549f, 0.85490197f),
                        GColorF.newRGB(0.54901963f, 0.5882353f, 0.7764706f),
                        GColorF.newRGB(0.54901963f, 0.41960785f, 0.69411767f),
                        GColorF.newRGB(0.53333336f, 0.25490198f, 0.6156863f),
                        GColorF.newRGB(0.43137255f, 0.003921569f, 0.41960785f)
      }));
      add(new GColorScheme("BuPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.99215686f),
                        GColorF.newRGB(0.8784314f, 0.9254902f, 0.95686275f),
                        GColorF.newRGB(0.7490196f, 0.827451f, 0.9019608f),
                        GColorF.newRGB(0.61960787f, 0.7372549f, 0.85490197f),
                        GColorF.newRGB(0.54901963f, 0.5882353f, 0.7764706f),
                        GColorF.newRGB(0.54901963f, 0.41960785f, 0.69411767f),
                        GColorF.newRGB(0.53333336f, 0.25490198f, 0.6156863f),
                        GColorF.newRGB(0.5058824f, 0.05882353f, 0.4862745f),
                        GColorF.newRGB(0.3019608f, 0.0f, 0.29411766f)
      }));


      // ColorScheme "Dark2"  Type=Qualitative
      add(new GColorScheme("Dark2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.105882354f, 0.61960787f, 0.46666667f),
                        GColorF.newRGB(0.45882353f, 0.4392157f, 0.7019608f)
      }));
      add(new GColorScheme("Dark2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.105882354f, 0.61960787f, 0.46666667f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.007843138f),
                        GColorF.newRGB(0.45882353f, 0.4392157f, 0.7019608f)
      }));
      add(new GColorScheme("Dark2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.105882354f, 0.61960787f, 0.46666667f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.007843138f),
                        GColorF.newRGB(0.45882353f, 0.4392157f, 0.7019608f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f)
      }));
      add(new GColorScheme("Dark2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.105882354f, 0.61960787f, 0.46666667f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.007843138f),
                        GColorF.newRGB(0.45882353f, 0.4392157f, 0.7019608f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f),
                        GColorF.newRGB(0.4f, 0.6509804f, 0.11764706f)
      }));
      add(new GColorScheme("Dark2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.105882354f, 0.61960787f, 0.46666667f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.007843138f),
                        GColorF.newRGB(0.45882353f, 0.4392157f, 0.7019608f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f),
                        GColorF.newRGB(0.4f, 0.6509804f, 0.11764706f),
                        GColorF.newRGB(0.9019608f, 0.67058825f, 0.007843138f)
      }));
      add(new GColorScheme("Dark2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.105882354f, 0.61960787f, 0.46666667f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.007843138f),
                        GColorF.newRGB(0.45882353f, 0.4392157f, 0.7019608f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f),
                        GColorF.newRGB(0.4f, 0.6509804f, 0.11764706f),
                        GColorF.newRGB(0.9019608f, 0.67058825f, 0.007843138f),
                        GColorF.newRGB(0.6509804f, 0.4627451f, 0.11372549f)
      }));
      add(new GColorScheme("Dark2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.105882354f, 0.61960787f, 0.46666667f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.007843138f),
                        GColorF.newRGB(0.45882353f, 0.4392157f, 0.7019608f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f),
                        GColorF.newRGB(0.4f, 0.6509804f, 0.11764706f),
                        GColorF.newRGB(0.9019608f, 0.67058825f, 0.007843138f),
                        GColorF.newRGB(0.6509804f, 0.4627451f, 0.11372549f),
                        GColorF.newRGB(0.4f, 0.4f, 0.4f)
      }));


      // ColorScheme "GnBu"  Type=Sequential
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.85882354f),
                        GColorF.newRGB(0.2627451f, 0.63529414f, 0.7921569f)
      }));
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.85882354f),
                        GColorF.newRGB(0.65882355f, 0.8666667f, 0.70980394f),
                        GColorF.newRGB(0.2627451f, 0.63529414f, 0.7921569f)
      }));
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9411765f, 0.9764706f, 0.9098039f),
                        GColorF.newRGB(0.7294118f, 0.89411765f, 0.7372549f),
                        GColorF.newRGB(0.48235294f, 0.8f, 0.76862746f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f)
      }));
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9411765f, 0.9764706f, 0.9098039f),
                        GColorF.newRGB(0.7294118f, 0.89411765f, 0.7372549f),
                        GColorF.newRGB(0.48235294f, 0.8f, 0.76862746f),
                        GColorF.newRGB(0.2627451f, 0.63529414f, 0.7921569f),
                        GColorF.newRGB(0.03137255f, 0.40784314f, 0.6745098f)
      }));
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9411765f, 0.9764706f, 0.9098039f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.65882355f, 0.8666667f, 0.70980394f),
                        GColorF.newRGB(0.48235294f, 0.8f, 0.76862746f),
                        GColorF.newRGB(0.2627451f, 0.63529414f, 0.7921569f),
                        GColorF.newRGB(0.03137255f, 0.40784314f, 0.6745098f)
      }));
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9411765f, 0.9764706f, 0.9098039f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.65882355f, 0.8666667f, 0.70980394f),
                        GColorF.newRGB(0.48235294f, 0.8f, 0.76862746f),
                        GColorF.newRGB(0.30588236f, 0.7019608f, 0.827451f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f),
                        GColorF.newRGB(0.03137255f, 0.34509805f, 0.61960787f)
      }));
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.9411765f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.85882354f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.65882355f, 0.8666667f, 0.70980394f),
                        GColorF.newRGB(0.48235294f, 0.8f, 0.76862746f),
                        GColorF.newRGB(0.30588236f, 0.7019608f, 0.827451f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f),
                        GColorF.newRGB(0.03137255f, 0.34509805f, 0.61960787f)
      }));
      add(new GColorScheme("GnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.9411765f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.85882354f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.65882355f, 0.8666667f, 0.70980394f),
                        GColorF.newRGB(0.48235294f, 0.8f, 0.76862746f),
                        GColorF.newRGB(0.30588236f, 0.7019608f, 0.827451f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f),
                        GColorF.newRGB(0.03137255f, 0.40784314f, 0.6745098f),
                        GColorF.newRGB(0.03137255f, 0.2509804f, 0.5058824f)
      }));


      // ColorScheme "Greens"  Type=Sequential
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.8784314f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f)
      }));
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.8784314f),
                        GColorF.newRGB(0.6313726f, 0.8509804f, 0.60784316f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f)
      }));
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9137255f),
                        GColorF.newRGB(0.7294118f, 0.89411765f, 0.7019608f),
                        GColorF.newRGB(0.45490196f, 0.76862746f, 0.4627451f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f)
      }));
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9137255f),
                        GColorF.newRGB(0.7294118f, 0.89411765f, 0.7019608f),
                        GColorF.newRGB(0.45490196f, 0.76862746f, 0.4627451f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f),
                        GColorF.newRGB(0.0f, 0.42745098f, 0.17254902f)
      }));
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9137255f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7529412f),
                        GColorF.newRGB(0.6313726f, 0.8509804f, 0.60784316f),
                        GColorF.newRGB(0.45490196f, 0.76862746f, 0.4627451f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f),
                        GColorF.newRGB(0.0f, 0.42745098f, 0.17254902f)
      }));
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.9137255f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7529412f),
                        GColorF.newRGB(0.6313726f, 0.8509804f, 0.60784316f),
                        GColorF.newRGB(0.45490196f, 0.76862746f, 0.4627451f),
                        GColorF.newRGB(0.25490198f, 0.67058825f, 0.3647059f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f),
                        GColorF.newRGB(0.0f, 0.3529412f, 0.19607843f)
      }));
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.9607843f),
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.8784314f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7529412f),
                        GColorF.newRGB(0.6313726f, 0.8509804f, 0.60784316f),
                        GColorF.newRGB(0.45490196f, 0.76862746f, 0.4627451f),
                        GColorF.newRGB(0.25490198f, 0.67058825f, 0.3647059f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f),
                        GColorF.newRGB(0.0f, 0.3529412f, 0.19607843f)
      }));
      add(new GColorScheme("Greens", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.9607843f),
                        GColorF.newRGB(0.8980392f, 0.9607843f, 0.8784314f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7529412f),
                        GColorF.newRGB(0.6313726f, 0.8509804f, 0.60784316f),
                        GColorF.newRGB(0.45490196f, 0.76862746f, 0.4627451f),
                        GColorF.newRGB(0.25490198f, 0.67058825f, 0.3647059f),
                        GColorF.newRGB(0.13725491f, 0.54509807f, 0.27058825f),
                        GColorF.newRGB(0.0f, 0.42745098f, 0.17254902f),
                        GColorF.newRGB(0.0f, 0.26666668f, 0.105882354f)
      }));


      // ColorScheme "Greys"  Type=Sequential
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9411765f, 0.9411765f, 0.9411765f),
                        GColorF.newRGB(0.3882353f, 0.3882353f, 0.3882353f)
      }));
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9411765f, 0.9411765f, 0.9411765f),
                        GColorF.newRGB(0.7411765f, 0.7411765f, 0.7411765f),
                        GColorF.newRGB(0.3882353f, 0.3882353f, 0.3882353f)
      }));
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.8f, 0.8f, 0.8f),
                        GColorF.newRGB(0.5882353f, 0.5882353f, 0.5882353f),
                        GColorF.newRGB(0.32156864f, 0.32156864f, 0.32156864f)
      }));
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.8f, 0.8f, 0.8f),
                        GColorF.newRGB(0.5882353f, 0.5882353f, 0.5882353f),
                        GColorF.newRGB(0.3882353f, 0.3882353f, 0.3882353f),
                        GColorF.newRGB(0.14509805f, 0.14509805f, 0.14509805f)
      }));
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f),
                        GColorF.newRGB(0.7411765f, 0.7411765f, 0.7411765f),
                        GColorF.newRGB(0.5882353f, 0.5882353f, 0.5882353f),
                        GColorF.newRGB(0.3882353f, 0.3882353f, 0.3882353f),
                        GColorF.newRGB(0.14509805f, 0.14509805f, 0.14509805f)
      }));
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f),
                        GColorF.newRGB(0.7411765f, 0.7411765f, 0.7411765f),
                        GColorF.newRGB(0.5882353f, 0.5882353f, 0.5882353f),
                        GColorF.newRGB(0.4509804f, 0.4509804f, 0.4509804f),
                        GColorF.newRGB(0.32156864f, 0.32156864f, 0.32156864f),
                        GColorF.newRGB(0.14509805f, 0.14509805f, 0.14509805f)
      }));
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 1.0f),
                        GColorF.newRGB(0.9411765f, 0.9411765f, 0.9411765f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f),
                        GColorF.newRGB(0.7411765f, 0.7411765f, 0.7411765f),
                        GColorF.newRGB(0.5882353f, 0.5882353f, 0.5882353f),
                        GColorF.newRGB(0.4509804f, 0.4509804f, 0.4509804f),
                        GColorF.newRGB(0.32156864f, 0.32156864f, 0.32156864f),
                        GColorF.newRGB(0.14509805f, 0.14509805f, 0.14509805f)
      }));
      add(new GColorScheme("Greys", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 1.0f),
                        GColorF.newRGB(0.9411765f, 0.9411765f, 0.9411765f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f),
                        GColorF.newRGB(0.7411765f, 0.7411765f, 0.7411765f),
                        GColorF.newRGB(0.5882353f, 0.5882353f, 0.5882353f),
                        GColorF.newRGB(0.4509804f, 0.4509804f, 0.4509804f),
                        GColorF.newRGB(0.32156864f, 0.32156864f, 0.32156864f),
                        GColorF.newRGB(0.14509805f, 0.14509805f, 0.14509805f),
                        GColorF.newRGB(0.0f, 0.0f, 0.0f)
      }));


      // ColorScheme "Oranges"  Type=Sequential
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9019608f, 0.80784315f),
                        GColorF.newRGB(0.9019608f, 0.33333334f, 0.050980393f)
      }));
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9019608f, 0.80784315f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.41960785f),
                        GColorF.newRGB(0.9019608f, 0.33333334f, 0.050980393f)
      }));
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92941177f, 0.87058824f),
                        GColorF.newRGB(0.99215686f, 0.74509805f, 0.52156866f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.8509804f, 0.2784314f, 0.003921569f)
      }));
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92941177f, 0.87058824f),
                        GColorF.newRGB(0.99215686f, 0.74509805f, 0.52156866f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.9019608f, 0.33333334f, 0.050980393f),
                        GColorF.newRGB(0.6509804f, 0.21176471f, 0.011764706f)
      }));
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92941177f, 0.87058824f),
                        GColorF.newRGB(0.99215686f, 0.8156863f, 0.63529414f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.41960785f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.9019608f, 0.33333334f, 0.050980393f),
                        GColorF.newRGB(0.6509804f, 0.21176471f, 0.011764706f)
      }));
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92941177f, 0.87058824f),
                        GColorF.newRGB(0.99215686f, 0.8156863f, 0.63529414f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.41960785f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.94509804f, 0.4117647f, 0.07450981f),
                        GColorF.newRGB(0.8509804f, 0.28235295f, 0.003921569f),
                        GColorF.newRGB(0.54901963f, 0.1764706f, 0.015686275f)
      }));
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.9607843f, 0.92156863f),
                        GColorF.newRGB(0.99607843f, 0.9019608f, 0.80784315f),
                        GColorF.newRGB(0.99215686f, 0.8156863f, 0.63529414f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.41960785f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.94509804f, 0.4117647f, 0.07450981f),
                        GColorF.newRGB(0.8509804f, 0.28235295f, 0.003921569f),
                        GColorF.newRGB(0.54901963f, 0.1764706f, 0.015686275f)
      }));
      add(new GColorScheme("Oranges", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.9607843f, 0.92156863f),
                        GColorF.newRGB(0.99607843f, 0.9019608f, 0.80784315f),
                        GColorF.newRGB(0.99215686f, 0.8156863f, 0.63529414f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.41960785f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.94509804f, 0.4117647f, 0.07450981f),
                        GColorF.newRGB(0.8509804f, 0.28235295f, 0.003921569f),
                        GColorF.newRGB(0.6509804f, 0.21176471f, 0.011764706f),
                        GColorF.newRGB(0.49803922f, 0.15294118f, 0.015686275f)
      }));


      // ColorScheme "OrRd"  Type=Sequential
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9098039f, 0.78431374f),
                        GColorF.newRGB(0.8901961f, 0.2901961f, 0.2f)
      }));
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9098039f, 0.78431374f),
                        GColorF.newRGB(0.99215686f, 0.73333335f, 0.5176471f),
                        GColorF.newRGB(0.8901961f, 0.2901961f, 0.2f)
      }));
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9411765f, 0.8509804f),
                        GColorF.newRGB(0.99215686f, 0.8f, 0.5411765f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.12156863f)
      }));
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9411765f, 0.8509804f),
                        GColorF.newRGB(0.99215686f, 0.8f, 0.5411765f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.8901961f, 0.2901961f, 0.2f),
                        GColorF.newRGB(0.7019608f, 0.0f, 0.0f)
      }));
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9411765f, 0.8509804f),
                        GColorF.newRGB(0.99215686f, 0.83137256f, 0.61960787f),
                        GColorF.newRGB(0.99215686f, 0.73333335f, 0.5176471f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.8901961f, 0.2901961f, 0.2f),
                        GColorF.newRGB(0.7019608f, 0.0f, 0.0f)
      }));
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.9411765f, 0.8509804f),
                        GColorF.newRGB(0.99215686f, 0.83137256f, 0.61960787f),
                        GColorF.newRGB(0.99215686f, 0.73333335f, 0.5176471f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.9372549f, 0.39607844f, 0.28235295f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.12156863f),
                        GColorF.newRGB(0.6f, 0.0f, 0.0f)
      }));
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9254902f),
                        GColorF.newRGB(0.99607843f, 0.9098039f, 0.78431374f),
                        GColorF.newRGB(0.99215686f, 0.83137256f, 0.61960787f),
                        GColorF.newRGB(0.99215686f, 0.73333335f, 0.5176471f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.9372549f, 0.39607844f, 0.28235295f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.12156863f),
                        GColorF.newRGB(0.6f, 0.0f, 0.0f)
      }));
      add(new GColorScheme("OrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9254902f),
                        GColorF.newRGB(0.99607843f, 0.9098039f, 0.78431374f),
                        GColorF.newRGB(0.99215686f, 0.83137256f, 0.61960787f),
                        GColorF.newRGB(0.99215686f, 0.73333335f, 0.5176471f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.9372549f, 0.39607844f, 0.28235295f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.12156863f),
                        GColorF.newRGB(0.7019608f, 0.0f, 0.0f),
                        GColorF.newRGB(0.49803922f, 0.0f, 0.0f)
      }));


      // ColorScheme "Paired"  Type=Qualitative
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.7490196f, 0.43529412f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.7490196f, 0.43529412f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.7490196f, 0.43529412f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(0.7921569f, 0.69803923f, 0.8392157f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.7490196f, 0.43529412f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(0.7921569f, 0.69803923f, 0.8392157f),
                        GColorF.newRGB(0.41568628f, 0.23921569f, 0.6039216f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.7490196f, 0.43529412f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(0.7921569f, 0.69803923f, 0.8392157f),
                        GColorF.newRGB(0.41568628f, 0.23921569f, 0.6039216f),
                        GColorF.newRGB(1.0f, 1.0f, 0.6f)
      }));
      add(new GColorScheme("Paired", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.6509804f, 0.80784315f, 0.8901961f),
                        GColorF.newRGB(0.12156863f, 0.47058824f, 0.7058824f),
                        GColorF.newRGB(0.69803923f, 0.8745098f, 0.5411765f),
                        GColorF.newRGB(0.2f, 0.627451f, 0.17254902f),
                        GColorF.newRGB(0.9843137f, 0.6039216f, 0.6f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.7490196f, 0.43529412f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(0.7921569f, 0.69803923f, 0.8392157f),
                        GColorF.newRGB(0.41568628f, 0.23921569f, 0.6039216f),
                        GColorF.newRGB(1.0f, 1.0f, 0.6f),
                        GColorF.newRGB(0.69411767f, 0.34901962f, 0.15686275f)
      }));


      // ColorScheme "Pastel1"  Type=Qualitative
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f)
      }));
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f)
      }));
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.87058824f, 0.79607844f, 0.89411765f)
      }));
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.87058824f, 0.79607844f, 0.89411765f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.6509804f)
      }));
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.87058824f, 0.79607844f, 0.89411765f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.6509804f),
                        GColorF.newRGB(1.0f, 1.0f, 0.8f)
      }));
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.87058824f, 0.79607844f, 0.89411765f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.6509804f),
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.8980392f, 0.84705883f, 0.7411765f)
      }));
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.87058824f, 0.79607844f, 0.89411765f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.6509804f),
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.8980392f, 0.84705883f, 0.7411765f),
                        GColorF.newRGB(0.99215686f, 0.85490197f, 0.9254902f)
      }));
      add(new GColorScheme("Pastel1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.68235296f),
                        GColorF.newRGB(0.7019608f, 0.8039216f, 0.8901961f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(0.87058824f, 0.79607844f, 0.89411765f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.6509804f),
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.8980392f, 0.84705883f, 0.7411765f),
                        GColorF.newRGB(0.99215686f, 0.85490197f, 0.9254902f),
                        GColorF.newRGB(0.9490196f, 0.9490196f, 0.9490196f)
      }));


      // ColorScheme "Pastel2"  Type=Qualitative
      add(new GColorScheme("Pastel2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.8862745f, 0.8039216f),
                        GColorF.newRGB(0.79607844f, 0.8352941f, 0.9098039f)
      }));
      add(new GColorScheme("Pastel2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.8862745f, 0.8039216f),
                        GColorF.newRGB(0.99215686f, 0.8039216f, 0.6745098f),
                        GColorF.newRGB(0.79607844f, 0.8352941f, 0.9098039f)
      }));
      add(new GColorScheme("Pastel2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.8862745f, 0.8039216f),
                        GColorF.newRGB(0.99215686f, 0.8039216f, 0.6745098f),
                        GColorF.newRGB(0.79607844f, 0.8352941f, 0.9098039f),
                        GColorF.newRGB(0.95686275f, 0.7921569f, 0.89411765f)
      }));
      add(new GColorScheme("Pastel2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.8862745f, 0.8039216f),
                        GColorF.newRGB(0.99215686f, 0.8039216f, 0.6745098f),
                        GColorF.newRGB(0.79607844f, 0.8352941f, 0.9098039f),
                        GColorF.newRGB(0.95686275f, 0.7921569f, 0.89411765f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.7882353f)
      }));
      add(new GColorScheme("Pastel2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.8862745f, 0.8039216f),
                        GColorF.newRGB(0.99215686f, 0.8039216f, 0.6745098f),
                        GColorF.newRGB(0.79607844f, 0.8352941f, 0.9098039f),
                        GColorF.newRGB(0.95686275f, 0.7921569f, 0.89411765f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.7882353f),
                        GColorF.newRGB(1.0f, 0.9490196f, 0.68235296f)
      }));
      add(new GColorScheme("Pastel2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.8862745f, 0.8039216f),
                        GColorF.newRGB(0.99215686f, 0.8039216f, 0.6745098f),
                        GColorF.newRGB(0.79607844f, 0.8352941f, 0.9098039f),
                        GColorF.newRGB(0.95686275f, 0.7921569f, 0.89411765f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.7882353f),
                        GColorF.newRGB(1.0f, 0.9490196f, 0.68235296f),
                        GColorF.newRGB(0.94509804f, 0.8862745f, 0.8f)
      }));
      add(new GColorScheme("Pastel2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.8862745f, 0.8039216f),
                        GColorF.newRGB(0.99215686f, 0.8039216f, 0.6745098f),
                        GColorF.newRGB(0.79607844f, 0.8352941f, 0.9098039f),
                        GColorF.newRGB(0.95686275f, 0.7921569f, 0.89411765f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.7882353f),
                        GColorF.newRGB(1.0f, 0.9490196f, 0.68235296f),
                        GColorF.newRGB(0.94509804f, 0.8862745f, 0.8f),
                        GColorF.newRGB(0.8f, 0.8f, 0.8f)
      }));


      // ColorScheme "PiYG"  Type=Diverging
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9137255f, 0.6392157f, 0.7882353f),
                        GColorF.newRGB(0.6313726f, 0.84313726f, 0.41568628f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9137255f, 0.6392157f, 0.7882353f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.6313726f, 0.84313726f, 0.41568628f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.8156863f, 0.10980392f, 0.54509807f),
                        GColorF.newRGB(0.94509804f, 0.7137255f, 0.85490197f),
                        GColorF.newRGB(0.72156864f, 0.88235295f, 0.5254902f),
                        GColorF.newRGB(0.3019608f, 0.6745098f, 0.14901961f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.8156863f, 0.10980392f, 0.54509807f),
                        GColorF.newRGB(0.94509804f, 0.7137255f, 0.85490197f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.72156864f, 0.88235295f, 0.5254902f),
                        GColorF.newRGB(0.3019608f, 0.6745098f, 0.14901961f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.49019608f),
                        GColorF.newRGB(0.9137255f, 0.6392157f, 0.7882353f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.9372549f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.8156863f),
                        GColorF.newRGB(0.6313726f, 0.84313726f, 0.41568628f),
                        GColorF.newRGB(0.3019608f, 0.57254905f, 0.12941177f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.49019608f),
                        GColorF.newRGB(0.9137255f, 0.6392157f, 0.7882353f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.9372549f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.8156863f),
                        GColorF.newRGB(0.6313726f, 0.84313726f, 0.41568628f),
                        GColorF.newRGB(0.3019608f, 0.57254905f, 0.12941177f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.49019608f),
                        GColorF.newRGB(0.87058824f, 0.46666667f, 0.68235296f),
                        GColorF.newRGB(0.94509804f, 0.7137255f, 0.85490197f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.9372549f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.8156863f),
                        GColorF.newRGB(0.72156864f, 0.88235295f, 0.5254902f),
                        GColorF.newRGB(0.49803922f, 0.7372549f, 0.25490198f),
                        GColorF.newRGB(0.3019608f, 0.57254905f, 0.12941177f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.49019608f),
                        GColorF.newRGB(0.87058824f, 0.46666667f, 0.68235296f),
                        GColorF.newRGB(0.94509804f, 0.7137255f, 0.85490197f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.9372549f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.8156863f),
                        GColorF.newRGB(0.72156864f, 0.88235295f, 0.5254902f),
                        GColorF.newRGB(0.49803922f, 0.7372549f, 0.25490198f),
                        GColorF.newRGB(0.3019608f, 0.57254905f, 0.12941177f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.5568628f, 0.003921569f, 0.32156864f),
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.49019608f),
                        GColorF.newRGB(0.87058824f, 0.46666667f, 0.68235296f),
                        GColorF.newRGB(0.94509804f, 0.7137255f, 0.85490197f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.9372549f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.8156863f),
                        GColorF.newRGB(0.72156864f, 0.88235295f, 0.5254902f),
                        GColorF.newRGB(0.49803922f, 0.7372549f, 0.25490198f),
                        GColorF.newRGB(0.3019608f, 0.57254905f, 0.12941177f),
                        GColorF.newRGB(0.15294118f, 0.39215687f, 0.09803922f)
      }));
      add(new GColorScheme("PiYG", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.5568628f, 0.003921569f, 0.32156864f),
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.49019608f),
                        GColorF.newRGB(0.87058824f, 0.46666667f, 0.68235296f),
                        GColorF.newRGB(0.94509804f, 0.7137255f, 0.85490197f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.9372549f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.8156863f),
                        GColorF.newRGB(0.72156864f, 0.88235295f, 0.5254902f),
                        GColorF.newRGB(0.49803922f, 0.7372549f, 0.25490198f),
                        GColorF.newRGB(0.3019608f, 0.57254905f, 0.12941177f),
                        GColorF.newRGB(0.15294118f, 0.39215687f, 0.09803922f)
      }));


      // ColorScheme "PRGn"  Type=Diverging
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.6862745f, 0.5529412f, 0.7647059f),
                        GColorF.newRGB(0.49803922f, 0.7490196f, 0.48235294f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.6862745f, 0.5529412f, 0.7647059f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.49803922f, 0.7490196f, 0.48235294f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.48235294f, 0.19607843f, 0.5803922f),
                        GColorF.newRGB(0.7607843f, 0.64705884f, 0.8117647f),
                        GColorF.newRGB(0.6509804f, 0.85882354f, 0.627451f),
                        GColorF.newRGB(0.0f, 0.53333336f, 0.21568628f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.48235294f, 0.19607843f, 0.5803922f),
                        GColorF.newRGB(0.7607843f, 0.64705884f, 0.8117647f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.6509804f, 0.85882354f, 0.627451f),
                        GColorF.newRGB(0.0f, 0.53333336f, 0.21568628f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.4627451f, 0.16470589f, 0.5137255f),
                        GColorF.newRGB(0.6862745f, 0.5529412f, 0.7647059f),
                        GColorF.newRGB(0.90588236f, 0.83137256f, 0.9098039f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.827451f),
                        GColorF.newRGB(0.49803922f, 0.7490196f, 0.48235294f),
                        GColorF.newRGB(0.105882354f, 0.47058824f, 0.21568628f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.4627451f, 0.16470589f, 0.5137255f),
                        GColorF.newRGB(0.6862745f, 0.5529412f, 0.7647059f),
                        GColorF.newRGB(0.90588236f, 0.83137256f, 0.9098039f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.827451f),
                        GColorF.newRGB(0.49803922f, 0.7490196f, 0.48235294f),
                        GColorF.newRGB(0.105882354f, 0.47058824f, 0.21568628f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.4627451f, 0.16470589f, 0.5137255f),
                        GColorF.newRGB(0.6f, 0.4392157f, 0.67058825f),
                        GColorF.newRGB(0.7607843f, 0.64705884f, 0.8117647f),
                        GColorF.newRGB(0.90588236f, 0.83137256f, 0.9098039f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.827451f),
                        GColorF.newRGB(0.6509804f, 0.85882354f, 0.627451f),
                        GColorF.newRGB(0.3529412f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.105882354f, 0.47058824f, 0.21568628f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.4627451f, 0.16470589f, 0.5137255f),
                        GColorF.newRGB(0.6f, 0.4392157f, 0.67058825f),
                        GColorF.newRGB(0.7607843f, 0.64705884f, 0.8117647f),
                        GColorF.newRGB(0.90588236f, 0.83137256f, 0.9098039f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.827451f),
                        GColorF.newRGB(0.6509804f, 0.85882354f, 0.627451f),
                        GColorF.newRGB(0.3529412f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.105882354f, 0.47058824f, 0.21568628f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.2509804f, 0.0f, 0.29411766f),
                        GColorF.newRGB(0.4627451f, 0.16470589f, 0.5137255f),
                        GColorF.newRGB(0.6f, 0.4392157f, 0.67058825f),
                        GColorF.newRGB(0.7607843f, 0.64705884f, 0.8117647f),
                        GColorF.newRGB(0.90588236f, 0.83137256f, 0.9098039f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.827451f),
                        GColorF.newRGB(0.6509804f, 0.85882354f, 0.627451f),
                        GColorF.newRGB(0.3529412f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.105882354f, 0.47058824f, 0.21568628f),
                        GColorF.newRGB(0.0f, 0.26666668f, 0.105882354f)
      }));
      add(new GColorScheme("PRGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.2509804f, 0.0f, 0.29411766f),
                        GColorF.newRGB(0.4627451f, 0.16470589f, 0.5137255f),
                        GColorF.newRGB(0.6f, 0.4392157f, 0.67058825f),
                        GColorF.newRGB(0.7607843f, 0.64705884f, 0.8117647f),
                        GColorF.newRGB(0.90588236f, 0.83137256f, 0.9098039f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.827451f),
                        GColorF.newRGB(0.6509804f, 0.85882354f, 0.627451f),
                        GColorF.newRGB(0.3529412f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.105882354f, 0.47058824f, 0.21568628f),
                        GColorF.newRGB(0.0f, 0.26666668f, 0.105882354f)
      }));


      // ColorScheme "PuBu"  Type=Sequential
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9254902f, 0.90588236f, 0.9490196f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f)
      }));
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9254902f, 0.90588236f, 0.9490196f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f)
      }));
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.7411765f, 0.7882353f, 0.88235295f),
                        GColorF.newRGB(0.45490196f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.019607844f, 0.4392157f, 0.6901961f)
      }));
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.7411765f, 0.7882353f, 0.88235295f),
                        GColorF.newRGB(0.45490196f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f),
                        GColorF.newRGB(0.015686275f, 0.3529412f, 0.5529412f)
      }));
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.45490196f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.16862746f, 0.54901963f, 0.74509805f),
                        GColorF.newRGB(0.015686275f, 0.3529412f, 0.5529412f)
      }));
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.45490196f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.21176471f, 0.5647059f, 0.7529412f),
                        GColorF.newRGB(0.019607844f, 0.4392157f, 0.6901961f),
                        GColorF.newRGB(0.011764706f, 0.30588236f, 0.48235294f)
      }));
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9843137f),
                        GColorF.newRGB(0.9254902f, 0.90588236f, 0.9490196f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.45490196f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.21176471f, 0.5647059f, 0.7529412f),
                        GColorF.newRGB(0.019607844f, 0.4392157f, 0.6901961f),
                        GColorF.newRGB(0.011764706f, 0.30588236f, 0.48235294f)
      }));
      add(new GColorScheme("PuBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9843137f),
                        GColorF.newRGB(0.9254902f, 0.90588236f, 0.9490196f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.45490196f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.21176471f, 0.5647059f, 0.7529412f),
                        GColorF.newRGB(0.019607844f, 0.4392157f, 0.6901961f),
                        GColorF.newRGB(0.015686275f, 0.3529412f, 0.5529412f),
                        GColorF.newRGB(0.007843138f, 0.21960784f, 0.34509805f)
      }));


      // ColorScheme "PuBuGn"  Type=Sequential
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9254902f, 0.8862745f, 0.9411765f),
                        GColorF.newRGB(0.10980392f, 0.5647059f, 0.6f)
      }));
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9254902f, 0.8862745f, 0.9411765f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.10980392f, 0.5647059f, 0.6f)
      }));
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9647059f, 0.9372549f, 0.96862745f),
                        GColorF.newRGB(0.7411765f, 0.7882353f, 0.88235295f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.007843138f, 0.5058824f, 0.5411765f)
      }));
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9647059f, 0.9372549f, 0.96862745f),
                        GColorF.newRGB(0.7411765f, 0.7882353f, 0.88235295f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.10980392f, 0.5647059f, 0.6f),
                        GColorF.newRGB(0.003921569f, 0.42352942f, 0.34901962f)
      }));
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9647059f, 0.9372549f, 0.96862745f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.10980392f, 0.5647059f, 0.6f),
                        GColorF.newRGB(0.003921569f, 0.42352942f, 0.34901962f)
      }));
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9647059f, 0.9372549f, 0.96862745f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.21176471f, 0.5647059f, 0.7529412f),
                        GColorF.newRGB(0.007843138f, 0.5058824f, 0.5411765f),
                        GColorF.newRGB(0.003921569f, 0.39215687f, 0.3137255f)
      }));
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9843137f),
                        GColorF.newRGB(0.9254902f, 0.8862745f, 0.9411765f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.21176471f, 0.5647059f, 0.7529412f),
                        GColorF.newRGB(0.007843138f, 0.5058824f, 0.5411765f),
                        GColorF.newRGB(0.003921569f, 0.39215687f, 0.3137255f)
      }));
      add(new GColorScheme("PuBuGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9843137f),
                        GColorF.newRGB(0.9254902f, 0.8862745f, 0.9411765f),
                        GColorF.newRGB(0.8156863f, 0.81960785f, 0.9019608f),
                        GColorF.newRGB(0.6509804f, 0.7411765f, 0.85882354f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.21176471f, 0.5647059f, 0.7529412f),
                        GColorF.newRGB(0.007843138f, 0.5058824f, 0.5411765f),
                        GColorF.newRGB(0.003921569f, 0.42352942f, 0.34901962f),
                        GColorF.newRGB(0.003921569f, 0.27450982f, 0.21176471f)
      }));


      // ColorScheme "PuOr"  Type=Diverging
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.6392157f, 0.2509804f),
                        GColorF.newRGB(0.6f, 0.5568628f, 0.7647059f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.6392157f, 0.2509804f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.6f, 0.5568628f, 0.7647059f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9019608f, 0.38039216f, 0.003921569f),
                        GColorF.newRGB(0.99215686f, 0.72156864f, 0.3882353f),
                        GColorF.newRGB(0.69803923f, 0.67058825f, 0.8235294f),
                        GColorF.newRGB(0.36862746f, 0.23529412f, 0.6f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9019608f, 0.38039216f, 0.003921569f),
                        GColorF.newRGB(0.99215686f, 0.72156864f, 0.3882353f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.69803923f, 0.67058825f, 0.8235294f),
                        GColorF.newRGB(0.36862746f, 0.23529412f, 0.6f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.34509805f, 0.023529412f),
                        GColorF.newRGB(0.94509804f, 0.6392157f, 0.2509804f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.7137255f),
                        GColorF.newRGB(0.84705883f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.6f, 0.5568628f, 0.7647059f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.53333336f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.34509805f, 0.023529412f),
                        GColorF.newRGB(0.94509804f, 0.6392157f, 0.2509804f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.7137255f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.84705883f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.6f, 0.5568628f, 0.7647059f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.53333336f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.34509805f, 0.023529412f),
                        GColorF.newRGB(0.8784314f, 0.50980395f, 0.078431375f),
                        GColorF.newRGB(0.99215686f, 0.72156864f, 0.3882353f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.7137255f),
                        GColorF.newRGB(0.84705883f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.69803923f, 0.67058825f, 0.8235294f),
                        GColorF.newRGB(0.5019608f, 0.4509804f, 0.6745098f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.53333336f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7019608f, 0.34509805f, 0.023529412f),
                        GColorF.newRGB(0.8784314f, 0.50980395f, 0.078431375f),
                        GColorF.newRGB(0.99215686f, 0.72156864f, 0.3882353f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.7137255f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.84705883f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.69803923f, 0.67058825f, 0.8235294f),
                        GColorF.newRGB(0.5019608f, 0.4509804f, 0.6745098f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.53333336f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.23137255f, 0.03137255f),
                        GColorF.newRGB(0.7019608f, 0.34509805f, 0.023529412f),
                        GColorF.newRGB(0.8784314f, 0.50980395f, 0.078431375f),
                        GColorF.newRGB(0.99215686f, 0.72156864f, 0.3882353f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.7137255f),
                        GColorF.newRGB(0.84705883f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.69803923f, 0.67058825f, 0.8235294f),
                        GColorF.newRGB(0.5019608f, 0.4509804f, 0.6745098f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.53333336f),
                        GColorF.newRGB(0.1764706f, 0.0f, 0.29411766f)
      }));
      add(new GColorScheme("PuOr", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.49803922f, 0.23137255f, 0.03137255f),
                        GColorF.newRGB(0.7019608f, 0.34509805f, 0.023529412f),
                        GColorF.newRGB(0.8784314f, 0.50980395f, 0.078431375f),
                        GColorF.newRGB(0.99215686f, 0.72156864f, 0.3882353f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.7137255f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.84705883f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.69803923f, 0.67058825f, 0.8235294f),
                        GColorF.newRGB(0.5019608f, 0.4509804f, 0.6745098f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.53333336f),
                        GColorF.newRGB(0.1764706f, 0.0f, 0.29411766f)
      }));


      // ColorScheme "PuRd"  Type=Sequential
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.90588236f, 0.88235295f, 0.9372549f),
                        GColorF.newRGB(0.8666667f, 0.10980392f, 0.46666667f)
      }));
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.90588236f, 0.88235295f, 0.9372549f),
                        GColorF.newRGB(0.7882353f, 0.5803922f, 0.78039217f),
                        GColorF.newRGB(0.8666667f, 0.10980392f, 0.46666667f)
      }));
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.84313726f, 0.70980394f, 0.84705883f),
                        GColorF.newRGB(0.8745098f, 0.39607844f, 0.6901961f),
                        GColorF.newRGB(0.80784315f, 0.07058824f, 0.3372549f)
      }));
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.84313726f, 0.70980394f, 0.84705883f),
                        GColorF.newRGB(0.8745098f, 0.39607844f, 0.6901961f),
                        GColorF.newRGB(0.8666667f, 0.10980392f, 0.46666667f),
                        GColorF.newRGB(0.59607846f, 0.0f, 0.2627451f)
      }));
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.83137256f, 0.7254902f, 0.85490197f),
                        GColorF.newRGB(0.7882353f, 0.5803922f, 0.78039217f),
                        GColorF.newRGB(0.8745098f, 0.39607844f, 0.6901961f),
                        GColorF.newRGB(0.8666667f, 0.10980392f, 0.46666667f),
                        GColorF.newRGB(0.59607846f, 0.0f, 0.2627451f)
      }));
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.94509804f, 0.93333334f, 0.9647059f),
                        GColorF.newRGB(0.83137256f, 0.7254902f, 0.85490197f),
                        GColorF.newRGB(0.7882353f, 0.5803922f, 0.78039217f),
                        GColorF.newRGB(0.8745098f, 0.39607844f, 0.6901961f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f),
                        GColorF.newRGB(0.80784315f, 0.07058824f, 0.3372549f),
                        GColorF.newRGB(0.5686275f, 0.0f, 0.24705882f)
      }));
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.95686275f, 0.9764706f),
                        GColorF.newRGB(0.90588236f, 0.88235295f, 0.9372549f),
                        GColorF.newRGB(0.83137256f, 0.7254902f, 0.85490197f),
                        GColorF.newRGB(0.7882353f, 0.5803922f, 0.78039217f),
                        GColorF.newRGB(0.8745098f, 0.39607844f, 0.6901961f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f),
                        GColorF.newRGB(0.80784315f, 0.07058824f, 0.3372549f),
                        GColorF.newRGB(0.5686275f, 0.0f, 0.24705882f)
      }));
      add(new GColorScheme("PuRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.95686275f, 0.9764706f),
                        GColorF.newRGB(0.90588236f, 0.88235295f, 0.9372549f),
                        GColorF.newRGB(0.83137256f, 0.7254902f, 0.85490197f),
                        GColorF.newRGB(0.7882353f, 0.5803922f, 0.78039217f),
                        GColorF.newRGB(0.8745098f, 0.39607844f, 0.6901961f),
                        GColorF.newRGB(0.90588236f, 0.16078432f, 0.5411765f),
                        GColorF.newRGB(0.80784315f, 0.07058824f, 0.3372549f),
                        GColorF.newRGB(0.59607846f, 0.0f, 0.2627451f),
                        GColorF.newRGB(0.40392157f, 0.0f, 0.12156863f)
      }));


      // ColorScheme "Purples"  Type=Sequential
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.92941177f, 0.9607843f),
                        GColorF.newRGB(0.45882353f, 0.41960785f, 0.69411767f)
      }));
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.92941177f, 0.9607843f),
                        GColorF.newRGB(0.7372549f, 0.7411765f, 0.8627451f),
                        GColorF.newRGB(0.45882353f, 0.41960785f, 0.69411767f)
      }));
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9490196f, 0.9411765f, 0.96862745f),
                        GColorF.newRGB(0.79607844f, 0.7882353f, 0.8862745f),
                        GColorF.newRGB(0.61960787f, 0.6039216f, 0.78431374f),
                        GColorF.newRGB(0.41568628f, 0.31764707f, 0.6392157f)
      }));
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9490196f, 0.9411765f, 0.96862745f),
                        GColorF.newRGB(0.79607844f, 0.7882353f, 0.8862745f),
                        GColorF.newRGB(0.61960787f, 0.6039216f, 0.78431374f),
                        GColorF.newRGB(0.45882353f, 0.41960785f, 0.69411767f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.56078434f)
      }));
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9490196f, 0.9411765f, 0.96862745f),
                        GColorF.newRGB(0.85490197f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.7372549f, 0.7411765f, 0.8627451f),
                        GColorF.newRGB(0.61960787f, 0.6039216f, 0.78431374f),
                        GColorF.newRGB(0.45882353f, 0.41960785f, 0.69411767f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.56078434f)
      }));
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9490196f, 0.9411765f, 0.96862745f),
                        GColorF.newRGB(0.85490197f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.7372549f, 0.7411765f, 0.8627451f),
                        GColorF.newRGB(0.61960787f, 0.6039216f, 0.78431374f),
                        GColorF.newRGB(0.5019608f, 0.49019608f, 0.7294118f),
                        GColorF.newRGB(0.41568628f, 0.31764707f, 0.6392157f),
                        GColorF.newRGB(0.2901961f, 0.078431375f, 0.5254902f)
      }));
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.9843137f, 0.99215686f),
                        GColorF.newRGB(0.9372549f, 0.92941177f, 0.9607843f),
                        GColorF.newRGB(0.85490197f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.7372549f, 0.7411765f, 0.8627451f),
                        GColorF.newRGB(0.61960787f, 0.6039216f, 0.78431374f),
                        GColorF.newRGB(0.5019608f, 0.49019608f, 0.7294118f),
                        GColorF.newRGB(0.41568628f, 0.31764707f, 0.6392157f),
                        GColorF.newRGB(0.2901961f, 0.078431375f, 0.5254902f)
      }));
      add(new GColorScheme("Purples", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.9843137f, 0.99215686f),
                        GColorF.newRGB(0.9372549f, 0.92941177f, 0.9607843f),
                        GColorF.newRGB(0.85490197f, 0.85490197f, 0.92156863f),
                        GColorF.newRGB(0.7372549f, 0.7411765f, 0.8627451f),
                        GColorF.newRGB(0.61960787f, 0.6039216f, 0.78431374f),
                        GColorF.newRGB(0.5019608f, 0.49019608f, 0.7294118f),
                        GColorF.newRGB(0.41568628f, 0.31764707f, 0.6392157f),
                        GColorF.newRGB(0.32941177f, 0.15294118f, 0.56078434f),
                        GColorF.newRGB(0.24705882f, 0.0f, 0.49019608f)
      }));


      // ColorScheme "RdBu"  Type=Diverging
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7921569f, 0.0f, 0.1254902f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.57254905f, 0.77254903f, 0.87058824f),
                        GColorF.newRGB(0.019607844f, 0.44313726f, 0.6901961f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7921569f, 0.0f, 0.1254902f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.57254905f, 0.77254903f, 0.87058824f),
                        GColorF.newRGB(0.019607844f, 0.44313726f, 0.6901961f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.81960785f, 0.8980392f, 0.9411765f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.12941177f, 0.4f, 0.6745098f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.81960785f, 0.8980392f, 0.9411765f),
                        GColorF.newRGB(0.40392157f, 0.6627451f, 0.8117647f),
                        GColorF.newRGB(0.12941177f, 0.4f, 0.6745098f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.81960785f, 0.8980392f, 0.9411765f),
                        GColorF.newRGB(0.57254905f, 0.77254903f, 0.87058824f),
                        GColorF.newRGB(0.2627451f, 0.5764706f, 0.7647059f),
                        GColorF.newRGB(0.12941177f, 0.4f, 0.6745098f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.81960785f, 0.8980392f, 0.9411765f),
                        GColorF.newRGB(0.57254905f, 0.77254903f, 0.87058824f),
                        GColorF.newRGB(0.2627451f, 0.5764706f, 0.7647059f),
                        GColorF.newRGB(0.12941177f, 0.4f, 0.6745098f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.40392157f, 0.0f, 0.12156863f),
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.81960785f, 0.8980392f, 0.9411765f),
                        GColorF.newRGB(0.57254905f, 0.77254903f, 0.87058824f),
                        GColorF.newRGB(0.2627451f, 0.5764706f, 0.7647059f),
                        GColorF.newRGB(0.12941177f, 0.4f, 0.6745098f),
                        GColorF.newRGB(0.019607844f, 0.1882353f, 0.38039216f)
      }));
      add(new GColorScheme("RdBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.40392157f, 0.0f, 0.12156863f),
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.96862745f, 0.96862745f, 0.96862745f),
                        GColorF.newRGB(0.81960785f, 0.8980392f, 0.9411765f),
                        GColorF.newRGB(0.57254905f, 0.77254903f, 0.87058824f),
                        GColorF.newRGB(0.2627451f, 0.5764706f, 0.7647059f),
                        GColorF.newRGB(0.12941177f, 0.4f, 0.6745098f),
                        GColorF.newRGB(0.019607844f, 0.1882353f, 0.38039216f)
      }));


      // ColorScheme "RdGy"  Type=Diverging
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(0.6f, 0.6f, 0.6f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(1.0f, 1.0f, 1.0f),
                        GColorF.newRGB(0.6f, 0.6f, 0.6f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7921569f, 0.0f, 0.1254902f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.7294118f, 0.7294118f, 0.7294118f),
                        GColorF.newRGB(0.2509804f, 0.2509804f, 0.2509804f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.7921569f, 0.0f, 0.1254902f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(1.0f, 1.0f, 1.0f),
                        GColorF.newRGB(0.7294118f, 0.7294118f, 0.7294118f),
                        GColorF.newRGB(0.2509804f, 0.2509804f, 0.2509804f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.8784314f, 0.8784314f, 0.8784314f),
                        GColorF.newRGB(0.6f, 0.6f, 0.6f),
                        GColorF.newRGB(0.3019608f, 0.3019608f, 0.3019608f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.9372549f, 0.5411765f, 0.38431373f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 1.0f),
                        GColorF.newRGB(0.8784314f, 0.8784314f, 0.8784314f),
                        GColorF.newRGB(0.6f, 0.6f, 0.6f),
                        GColorF.newRGB(0.3019608f, 0.3019608f, 0.3019608f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.8784314f, 0.8784314f, 0.8784314f),
                        GColorF.newRGB(0.7294118f, 0.7294118f, 0.7294118f),
                        GColorF.newRGB(0.5294118f, 0.5294118f, 0.5294118f),
                        GColorF.newRGB(0.3019608f, 0.3019608f, 0.3019608f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 1.0f),
                        GColorF.newRGB(0.8784314f, 0.8784314f, 0.8784314f),
                        GColorF.newRGB(0.7294118f, 0.7294118f, 0.7294118f),
                        GColorF.newRGB(0.5294118f, 0.5294118f, 0.5294118f),
                        GColorF.newRGB(0.3019608f, 0.3019608f, 0.3019608f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.40392157f, 0.0f, 0.12156863f),
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(0.8784314f, 0.8784314f, 0.8784314f),
                        GColorF.newRGB(0.7294118f, 0.7294118f, 0.7294118f),
                        GColorF.newRGB(0.5294118f, 0.5294118f, 0.5294118f),
                        GColorF.newRGB(0.3019608f, 0.3019608f, 0.3019608f),
                        GColorF.newRGB(0.101960786f, 0.101960786f, 0.101960786f)
      }));
      add(new GColorScheme("RdGy", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.40392157f, 0.0f, 0.12156863f),
                        GColorF.newRGB(0.69803923f, 0.09411765f, 0.16862746f),
                        GColorF.newRGB(0.8392157f, 0.3764706f, 0.3019608f),
                        GColorF.newRGB(0.95686275f, 0.64705884f, 0.50980395f),
                        GColorF.newRGB(0.99215686f, 0.85882354f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 1.0f),
                        GColorF.newRGB(0.8784314f, 0.8784314f, 0.8784314f),
                        GColorF.newRGB(0.7294118f, 0.7294118f, 0.7294118f),
                        GColorF.newRGB(0.5294118f, 0.5294118f, 0.5294118f),
                        GColorF.newRGB(0.3019608f, 0.3019608f, 0.3019608f),
                        GColorF.newRGB(0.101960786f, 0.101960786f, 0.101960786f)
      }));


      // ColorScheme "RdPu"  Type=Sequential
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.8666667f),
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.5411765f)
      }));
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.8666667f),
                        GColorF.newRGB(0.98039216f, 0.62352943f, 0.70980394f),
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.5411765f)
      }));
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92156863f, 0.8862745f),
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.7254902f),
                        GColorF.newRGB(0.96862745f, 0.40784314f, 0.6313726f),
                        GColorF.newRGB(0.68235296f, 0.003921569f, 0.49411765f)
      }));
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92156863f, 0.8862745f),
                        GColorF.newRGB(0.9843137f, 0.7058824f, 0.7254902f),
                        GColorF.newRGB(0.96862745f, 0.40784314f, 0.6313726f),
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.5411765f),
                        GColorF.newRGB(0.47843137f, 0.003921569f, 0.46666667f)
      }));
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92156863f, 0.8862745f),
                        GColorF.newRGB(0.9882353f, 0.77254903f, 0.7529412f),
                        GColorF.newRGB(0.98039216f, 0.62352943f, 0.70980394f),
                        GColorF.newRGB(0.96862745f, 0.40784314f, 0.6313726f),
                        GColorF.newRGB(0.77254903f, 0.105882354f, 0.5411765f),
                        GColorF.newRGB(0.47843137f, 0.003921569f, 0.46666667f)
      }));
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.92156863f, 0.8862745f),
                        GColorF.newRGB(0.9882353f, 0.77254903f, 0.7529412f),
                        GColorF.newRGB(0.98039216f, 0.62352943f, 0.70980394f),
                        GColorF.newRGB(0.96862745f, 0.40784314f, 0.6313726f),
                        GColorF.newRGB(0.8666667f, 0.20392157f, 0.5921569f),
                        GColorF.newRGB(0.68235296f, 0.003921569f, 0.49411765f),
                        GColorF.newRGB(0.47843137f, 0.003921569f, 0.46666667f)
      }));
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9529412f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.8666667f),
                        GColorF.newRGB(0.9882353f, 0.77254903f, 0.7529412f),
                        GColorF.newRGB(0.98039216f, 0.62352943f, 0.70980394f),
                        GColorF.newRGB(0.96862745f, 0.40784314f, 0.6313726f),
                        GColorF.newRGB(0.8666667f, 0.20392157f, 0.5921569f),
                        GColorF.newRGB(0.68235296f, 0.003921569f, 0.49411765f),
                        GColorF.newRGB(0.47843137f, 0.003921569f, 0.46666667f)
      }));
      add(new GColorScheme("RdPu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.9529412f),
                        GColorF.newRGB(0.99215686f, 0.8784314f, 0.8666667f),
                        GColorF.newRGB(0.9882353f, 0.77254903f, 0.7529412f),
                        GColorF.newRGB(0.98039216f, 0.62352943f, 0.70980394f),
                        GColorF.newRGB(0.96862745f, 0.40784314f, 0.6313726f),
                        GColorF.newRGB(0.8666667f, 0.20392157f, 0.5921569f),
                        GColorF.newRGB(0.68235296f, 0.003921569f, 0.49411765f),
                        GColorF.newRGB(0.47843137f, 0.003921569f, 0.46666667f),
                        GColorF.newRGB(0.28627452f, 0.0f, 0.41568628f)
      }));


      // ColorScheme "Reds"  Type=Sequential
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.8235294f),
                        GColorF.newRGB(0.87058824f, 0.1764706f, 0.14901961f)
      }));
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.8235294f),
                        GColorF.newRGB(0.9882353f, 0.57254905f, 0.44705883f),
                        GColorF.newRGB(0.87058824f, 0.1764706f, 0.14901961f)
      }));
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.8980392f, 0.8509804f),
                        GColorF.newRGB(0.9882353f, 0.68235296f, 0.5686275f),
                        GColorF.newRGB(0.9843137f, 0.41568628f, 0.2901961f),
                        GColorF.newRGB(0.79607844f, 0.09411765f, 0.11372549f)
      }));
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.8980392f, 0.8509804f),
                        GColorF.newRGB(0.9882353f, 0.68235296f, 0.5686275f),
                        GColorF.newRGB(0.9843137f, 0.41568628f, 0.2901961f),
                        GColorF.newRGB(0.87058824f, 0.1764706f, 0.14901961f),
                        GColorF.newRGB(0.64705884f, 0.05882353f, 0.08235294f)
      }));
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.8980392f, 0.8509804f),
                        GColorF.newRGB(0.9882353f, 0.73333335f, 0.6313726f),
                        GColorF.newRGB(0.9882353f, 0.57254905f, 0.44705883f),
                        GColorF.newRGB(0.9843137f, 0.41568628f, 0.2901961f),
                        GColorF.newRGB(0.87058824f, 0.1764706f, 0.14901961f),
                        GColorF.newRGB(0.64705884f, 0.05882353f, 0.08235294f)
      }));
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.99607843f, 0.8980392f, 0.8509804f),
                        GColorF.newRGB(0.9882353f, 0.73333335f, 0.6313726f),
                        GColorF.newRGB(0.9882353f, 0.57254905f, 0.44705883f),
                        GColorF.newRGB(0.9843137f, 0.41568628f, 0.2901961f),
                        GColorF.newRGB(0.9372549f, 0.23137255f, 0.17254902f),
                        GColorF.newRGB(0.79607844f, 0.09411765f, 0.11372549f),
                        GColorF.newRGB(0.6f, 0.0f, 0.050980393f)
      }));
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.9607843f, 0.9411765f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.8235294f),
                        GColorF.newRGB(0.9882353f, 0.73333335f, 0.6313726f),
                        GColorF.newRGB(0.9882353f, 0.57254905f, 0.44705883f),
                        GColorF.newRGB(0.9843137f, 0.41568628f, 0.2901961f),
                        GColorF.newRGB(0.9372549f, 0.23137255f, 0.17254902f),
                        GColorF.newRGB(0.79607844f, 0.09411765f, 0.11372549f),
                        GColorF.newRGB(0.6f, 0.0f, 0.050980393f)
      }));
      add(new GColorScheme("Reds", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.9607843f, 0.9411765f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.8235294f),
                        GColorF.newRGB(0.9882353f, 0.73333335f, 0.6313726f),
                        GColorF.newRGB(0.9882353f, 0.57254905f, 0.44705883f),
                        GColorF.newRGB(0.9843137f, 0.41568628f, 0.2901961f),
                        GColorF.newRGB(0.9372549f, 0.23137255f, 0.17254902f),
                        GColorF.newRGB(0.79607844f, 0.09411765f, 0.11372549f),
                        GColorF.newRGB(0.64705884f, 0.05882353f, 0.08235294f),
                        GColorF.newRGB(0.40392157f, 0.0f, 0.050980393f)
      }));


      // ColorScheme "RdYlBu"  Type=Diverging
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.5686275f, 0.7490196f, 0.85882354f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.5686275f, 0.7490196f, 0.85882354f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.09803922f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.67058825f, 0.8509804f, 0.9137255f),
                        GColorF.newRGB(0.17254902f, 0.48235294f, 0.7137255f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.09803922f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.67058825f, 0.8509804f, 0.9137255f),
                        GColorF.newRGB(0.17254902f, 0.48235294f, 0.7137255f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.5647059f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.972549f),
                        GColorF.newRGB(0.5686275f, 0.7490196f, 0.85882354f),
                        GColorF.newRGB(0.27058825f, 0.45882353f, 0.7058824f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.5647059f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.972549f),
                        GColorF.newRGB(0.5686275f, 0.7490196f, 0.85882354f),
                        GColorF.newRGB(0.27058825f, 0.45882353f, 0.7058824f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.5647059f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.972549f),
                        GColorF.newRGB(0.67058825f, 0.8509804f, 0.9137255f),
                        GColorF.newRGB(0.45490196f, 0.6784314f, 0.81960785f),
                        GColorF.newRGB(0.27058825f, 0.45882353f, 0.7058824f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.5647059f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.972549f),
                        GColorF.newRGB(0.67058825f, 0.8509804f, 0.9137255f),
                        GColorF.newRGB(0.45490196f, 0.6784314f, 0.81960785f),
                        GColorF.newRGB(0.27058825f, 0.45882353f, 0.7058824f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.64705884f, 0.0f, 0.14901961f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.5647059f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.972549f),
                        GColorF.newRGB(0.67058825f, 0.8509804f, 0.9137255f),
                        GColorF.newRGB(0.45490196f, 0.6784314f, 0.81960785f),
                        GColorF.newRGB(0.27058825f, 0.45882353f, 0.7058824f),
                        GColorF.newRGB(0.19215687f, 0.21176471f, 0.58431375f)
      }));
      add(new GColorScheme("RdYlBu", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.64705884f, 0.0f, 0.14901961f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.5647059f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.8784314f, 0.9529412f, 0.972549f),
                        GColorF.newRGB(0.67058825f, 0.8509804f, 0.9137255f),
                        GColorF.newRGB(0.45490196f, 0.6784314f, 0.81960785f),
                        GColorF.newRGB(0.27058825f, 0.45882353f, 0.7058824f),
                        GColorF.newRGB(0.19215687f, 0.21176471f, 0.58431375f)
      }));


      // ColorScheme "RdYlGn"  Type=Diverging
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.5686275f, 0.8117647f, 0.3764706f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.5686275f, 0.8117647f, 0.3764706f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.09803922f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.6509804f, 0.8509804f, 0.41568628f),
                        GColorF.newRGB(0.101960786f, 0.5882353f, 0.25490198f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.09803922f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.6509804f, 0.8509804f, 0.41568628f),
                        GColorF.newRGB(0.101960786f, 0.5882353f, 0.25490198f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(0.8509804f, 0.9372549f, 0.54509807f),
                        GColorF.newRGB(0.5686275f, 0.8117647f, 0.3764706f),
                        GColorF.newRGB(0.101960786f, 0.59607846f, 0.3137255f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.8509804f, 0.9372549f, 0.54509807f),
                        GColorF.newRGB(0.5686275f, 0.8117647f, 0.3764706f),
                        GColorF.newRGB(0.101960786f, 0.59607846f, 0.3137255f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(0.8509804f, 0.9372549f, 0.54509807f),
                        GColorF.newRGB(0.6509804f, 0.8509804f, 0.41568628f),
                        GColorF.newRGB(0.4f, 0.7411765f, 0.3882353f),
                        GColorF.newRGB(0.101960786f, 0.59607846f, 0.3137255f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.8509804f, 0.9372549f, 0.54509807f),
                        GColorF.newRGB(0.6509804f, 0.8509804f, 0.41568628f),
                        GColorF.newRGB(0.4f, 0.7411765f, 0.3882353f),
                        GColorF.newRGB(0.101960786f, 0.59607846f, 0.3137255f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.64705884f, 0.0f, 0.14901961f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(0.8509804f, 0.9372549f, 0.54509807f),
                        GColorF.newRGB(0.6509804f, 0.8509804f, 0.41568628f),
                        GColorF.newRGB(0.4f, 0.7411765f, 0.3882353f),
                        GColorF.newRGB(0.101960786f, 0.59607846f, 0.3137255f),
                        GColorF.newRGB(0.0f, 0.40784314f, 0.21568628f)
      }));
      add(new GColorScheme("RdYlGn", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.64705884f, 0.0f, 0.14901961f),
                        GColorF.newRGB(0.84313726f, 0.1882353f, 0.15294118f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.8509804f, 0.9372549f, 0.54509807f),
                        GColorF.newRGB(0.6509804f, 0.8509804f, 0.41568628f),
                        GColorF.newRGB(0.4f, 0.7411765f, 0.3882353f),
                        GColorF.newRGB(0.101960786f, 0.59607846f, 0.3137255f),
                        GColorF.newRGB(0.0f, 0.40784314f, 0.21568628f)
      }));


      // ColorScheme "Set1"  Type=Qualitative
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f)
      }));
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.21568628f, 0.49411765f, 0.72156864f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f)
      }));
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.21568628f, 0.49411765f, 0.72156864f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f),
                        GColorF.newRGB(0.59607846f, 0.30588236f, 0.6392157f)
      }));
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.21568628f, 0.49411765f, 0.72156864f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f),
                        GColorF.newRGB(0.59607846f, 0.30588236f, 0.6392157f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f)
      }));
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.21568628f, 0.49411765f, 0.72156864f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f),
                        GColorF.newRGB(0.59607846f, 0.30588236f, 0.6392157f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(1.0f, 1.0f, 0.2f)
      }));
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.21568628f, 0.49411765f, 0.72156864f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f),
                        GColorF.newRGB(0.59607846f, 0.30588236f, 0.6392157f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(1.0f, 1.0f, 0.2f),
                        GColorF.newRGB(0.6509804f, 0.3372549f, 0.15686275f)
      }));
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.21568628f, 0.49411765f, 0.72156864f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f),
                        GColorF.newRGB(0.59607846f, 0.30588236f, 0.6392157f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(1.0f, 1.0f, 0.2f),
                        GColorF.newRGB(0.6509804f, 0.3372549f, 0.15686275f),
                        GColorF.newRGB(0.96862745f, 0.5058824f, 0.7490196f)
      }));
      add(new GColorScheme("Set1", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.89411765f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.21568628f, 0.49411765f, 0.72156864f),
                        GColorF.newRGB(0.3019608f, 0.6862745f, 0.2901961f),
                        GColorF.newRGB(0.59607846f, 0.30588236f, 0.6392157f),
                        GColorF.newRGB(1.0f, 0.49803922f, 0.0f),
                        GColorF.newRGB(1.0f, 1.0f, 0.2f),
                        GColorF.newRGB(0.6509804f, 0.3372549f, 0.15686275f),
                        GColorF.newRGB(0.96862745f, 0.5058824f, 0.7490196f),
                        GColorF.newRGB(0.6f, 0.6f, 0.6f)
      }));


      // ColorScheme "Set2"  Type=Qualitative
      add(new GColorScheme("Set2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.5529412f, 0.627451f, 0.79607844f)
      }));
      add(new GColorScheme("Set2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.38431373f),
                        GColorF.newRGB(0.5529412f, 0.627451f, 0.79607844f)
      }));
      add(new GColorScheme("Set2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.38431373f),
                        GColorF.newRGB(0.5529412f, 0.627451f, 0.79607844f),
                        GColorF.newRGB(0.90588236f, 0.5411765f, 0.7647059f)
      }));
      add(new GColorScheme("Set2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.38431373f),
                        GColorF.newRGB(0.5529412f, 0.627451f, 0.79607844f),
                        GColorF.newRGB(0.90588236f, 0.5411765f, 0.7647059f),
                        GColorF.newRGB(0.6509804f, 0.84705883f, 0.32941177f)
      }));
      add(new GColorScheme("Set2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.38431373f),
                        GColorF.newRGB(0.5529412f, 0.627451f, 0.79607844f),
                        GColorF.newRGB(0.90588236f, 0.5411765f, 0.7647059f),
                        GColorF.newRGB(0.6509804f, 0.84705883f, 0.32941177f),
                        GColorF.newRGB(1.0f, 0.8509804f, 0.18431373f)
      }));
      add(new GColorScheme("Set2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.38431373f),
                        GColorF.newRGB(0.5529412f, 0.627451f, 0.79607844f),
                        GColorF.newRGB(0.90588236f, 0.5411765f, 0.7647059f),
                        GColorF.newRGB(0.6509804f, 0.84705883f, 0.32941177f),
                        GColorF.newRGB(1.0f, 0.8509804f, 0.18431373f),
                        GColorF.newRGB(0.8980392f, 0.76862746f, 0.5803922f)
      }));
      add(new GColorScheme("Set2", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.38431373f),
                        GColorF.newRGB(0.5529412f, 0.627451f, 0.79607844f),
                        GColorF.newRGB(0.90588236f, 0.5411765f, 0.7647059f),
                        GColorF.newRGB(0.6509804f, 0.84705883f, 0.32941177f),
                        GColorF.newRGB(1.0f, 0.8509804f, 0.18431373f),
                        GColorF.newRGB(0.8980392f, 0.76862746f, 0.5803922f),
                        GColorF.newRGB(0.7019608f, 0.7019608f, 0.7019608f)
      }));


      // ColorScheme "Set3"  Type=Qualitative
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f),
                        GColorF.newRGB(0.99215686f, 0.7058824f, 0.38431373f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f),
                        GColorF.newRGB(0.99215686f, 0.7058824f, 0.38431373f),
                        GColorF.newRGB(0.7019608f, 0.87058824f, 0.4117647f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f),
                        GColorF.newRGB(0.99215686f, 0.7058824f, 0.38431373f),
                        GColorF.newRGB(0.7019608f, 0.87058824f, 0.4117647f),
                        GColorF.newRGB(0.9882353f, 0.8039216f, 0.8980392f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f),
                        GColorF.newRGB(0.99215686f, 0.7058824f, 0.38431373f),
                        GColorF.newRGB(0.7019608f, 0.87058824f, 0.4117647f),
                        GColorF.newRGB(0.9882353f, 0.8039216f, 0.8980392f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f),
                        GColorF.newRGB(0.99215686f, 0.7058824f, 0.38431373f),
                        GColorF.newRGB(0.7019608f, 0.87058824f, 0.4117647f),
                        GColorF.newRGB(0.9882353f, 0.8039216f, 0.8980392f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f),
                        GColorF.newRGB(0.7372549f, 0.5019608f, 0.7411765f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f),
                        GColorF.newRGB(0.99215686f, 0.7058824f, 0.38431373f),
                        GColorF.newRGB(0.7019608f, 0.87058824f, 0.4117647f),
                        GColorF.newRGB(0.9882353f, 0.8039216f, 0.8980392f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f),
                        GColorF.newRGB(0.7372549f, 0.5019608f, 0.7411765f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f)
      }));
      add(new GColorScheme("Set3", GColorScheme.Type.Qualitative, new IColor[] {
                        GColorF.newRGB(0.5529412f, 0.827451f, 0.78039217f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7019608f),
                        GColorF.newRGB(0.74509805f, 0.7294118f, 0.85490197f),
                        GColorF.newRGB(0.9843137f, 0.5019608f, 0.44705883f),
                        GColorF.newRGB(0.5019608f, 0.69411767f, 0.827451f),
                        GColorF.newRGB(0.99215686f, 0.7058824f, 0.38431373f),
                        GColorF.newRGB(0.7019608f, 0.87058824f, 0.4117647f),
                        GColorF.newRGB(0.9882353f, 0.8039216f, 0.8980392f),
                        GColorF.newRGB(0.8509804f, 0.8509804f, 0.8509804f),
                        GColorF.newRGB(0.7372549f, 0.5019608f, 0.7411765f),
                        GColorF.newRGB(0.8f, 0.92156863f, 0.77254903f),
                        GColorF.newRGB(1.0f, 0.92941177f, 0.43529412f)
      }));


      // ColorScheme "Spectral"  Type=Diverging
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.6f, 0.8352941f, 0.5803922f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.6f, 0.8352941f, 0.5803922f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.09803922f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.67058825f, 0.8666667f, 0.6431373f),
                        GColorF.newRGB(0.16862746f, 0.5137255f, 0.7294118f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.84313726f, 0.09803922f, 0.10980392f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.67058825f, 0.8666667f, 0.6431373f),
                        GColorF.newRGB(0.16862746f, 0.5137255f, 0.7294118f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.8352941f, 0.24313726f, 0.30980393f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.59607846f),
                        GColorF.newRGB(0.6f, 0.8352941f, 0.5803922f),
                        GColorF.newRGB(0.19607843f, 0.53333336f, 0.7411765f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.8352941f, 0.24313726f, 0.30980393f),
                        GColorF.newRGB(0.9882353f, 0.5529412f, 0.34901962f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.59607846f),
                        GColorF.newRGB(0.6f, 0.8352941f, 0.5803922f),
                        GColorF.newRGB(0.19607843f, 0.53333336f, 0.7411765f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.8352941f, 0.24313726f, 0.30980393f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.59607846f),
                        GColorF.newRGB(0.67058825f, 0.8666667f, 0.6431373f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.19607843f, 0.53333336f, 0.7411765f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.8352941f, 0.24313726f, 0.30980393f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.59607846f),
                        GColorF.newRGB(0.67058825f, 0.8666667f, 0.6431373f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.19607843f, 0.53333336f, 0.7411765f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.61960787f, 0.003921569f, 0.25882354f),
                        GColorF.newRGB(0.8352941f, 0.24313726f, 0.30980393f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.59607846f),
                        GColorF.newRGB(0.67058825f, 0.8666667f, 0.6431373f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.19607843f, 0.53333336f, 0.7411765f),
                        GColorF.newRGB(0.36862746f, 0.30980393f, 0.63529414f)
      }));
      add(new GColorScheme("Spectral", GColorScheme.Type.Diverging, new IColor[] {
                        GColorF.newRGB(0.61960787f, 0.003921569f, 0.25882354f),
                        GColorF.newRGB(0.8352941f, 0.24313726f, 0.30980393f),
                        GColorF.newRGB(0.95686275f, 0.42745098f, 0.2627451f),
                        GColorF.newRGB(0.99215686f, 0.68235296f, 0.38039216f),
                        GColorF.newRGB(0.99607843f, 0.8784314f, 0.54509807f),
                        GColorF.newRGB(1.0f, 1.0f, 0.7490196f),
                        GColorF.newRGB(0.9019608f, 0.9607843f, 0.59607846f),
                        GColorF.newRGB(0.67058825f, 0.8666667f, 0.6431373f),
                        GColorF.newRGB(0.4f, 0.7607843f, 0.64705884f),
                        GColorF.newRGB(0.19607843f, 0.53333336f, 0.7411765f),
                        GColorF.newRGB(0.36862746f, 0.30980393f, 0.63529414f)
      }));


      // ColorScheme "YlGn"  Type=Sequential
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.7254902f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f)
      }));
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.7254902f),
                        GColorF.newRGB(0.6784314f, 0.8666667f, 0.5568628f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f)
      }));
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.7607843f, 0.9019608f, 0.6f),
                        GColorF.newRGB(0.47058824f, 0.7764706f, 0.4745098f),
                        GColorF.newRGB(0.13725491f, 0.5176471f, 0.2627451f)
      }));
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.7607843f, 0.9019608f, 0.6f),
                        GColorF.newRGB(0.47058824f, 0.7764706f, 0.4745098f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f),
                        GColorF.newRGB(0.0f, 0.40784314f, 0.21568628f)
      }));
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.6392157f),
                        GColorF.newRGB(0.6784314f, 0.8666667f, 0.5568628f),
                        GColorF.newRGB(0.47058824f, 0.7764706f, 0.4745098f),
                        GColorF.newRGB(0.19215687f, 0.6392157f, 0.32941177f),
                        GColorF.newRGB(0.0f, 0.40784314f, 0.21568628f)
      }));
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.6392157f),
                        GColorF.newRGB(0.6784314f, 0.8666667f, 0.5568628f),
                        GColorF.newRGB(0.47058824f, 0.7764706f, 0.4745098f),
                        GColorF.newRGB(0.25490198f, 0.67058825f, 0.3647059f),
                        GColorF.newRGB(0.13725491f, 0.5176471f, 0.2627451f),
                        GColorF.newRGB(0.0f, 0.3529412f, 0.19607843f)
      }));
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8980392f),
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.7254902f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.6392157f),
                        GColorF.newRGB(0.6784314f, 0.8666667f, 0.5568628f),
                        GColorF.newRGB(0.47058824f, 0.7764706f, 0.4745098f),
                        GColorF.newRGB(0.25490198f, 0.67058825f, 0.3647059f),
                        GColorF.newRGB(0.13725491f, 0.5176471f, 0.2627451f),
                        GColorF.newRGB(0.0f, 0.3529412f, 0.19607843f)
      }));
      add(new GColorScheme("YlGn", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8980392f),
                        GColorF.newRGB(0.96862745f, 0.9882353f, 0.7254902f),
                        GColorF.newRGB(0.8509804f, 0.9411765f, 0.6392157f),
                        GColorF.newRGB(0.6784314f, 0.8666667f, 0.5568628f),
                        GColorF.newRGB(0.47058824f, 0.7764706f, 0.4745098f),
                        GColorF.newRGB(0.25490198f, 0.67058825f, 0.3647059f),
                        GColorF.newRGB(0.13725491f, 0.5176471f, 0.2627451f),
                        GColorF.newRGB(0.0f, 0.40784314f, 0.21568628f),
                        GColorF.newRGB(0.0f, 0.27058825f, 0.16078432f)
      }));


      // ColorScheme "YlGnBu"  Type=Sequential
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.69411767f),
                        GColorF.newRGB(0.17254902f, 0.49803922f, 0.72156864f)
      }));
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.69411767f),
                        GColorF.newRGB(0.49803922f, 0.8039216f, 0.73333335f),
                        GColorF.newRGB(0.17254902f, 0.49803922f, 0.72156864f)
      }));
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.6313726f, 0.85490197f, 0.7058824f),
                        GColorF.newRGB(0.25490198f, 0.7137255f, 0.76862746f),
                        GColorF.newRGB(0.13333334f, 0.36862746f, 0.65882355f)
      }));
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.6313726f, 0.85490197f, 0.7058824f),
                        GColorF.newRGB(0.25490198f, 0.7137255f, 0.76862746f),
                        GColorF.newRGB(0.17254902f, 0.49803922f, 0.72156864f),
                        GColorF.newRGB(0.14509805f, 0.20392157f, 0.5803922f)
      }));
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7058824f),
                        GColorF.newRGB(0.49803922f, 0.8039216f, 0.73333335f),
                        GColorF.newRGB(0.25490198f, 0.7137255f, 0.76862746f),
                        GColorF.newRGB(0.17254902f, 0.49803922f, 0.72156864f),
                        GColorF.newRGB(0.14509805f, 0.20392157f, 0.5803922f)
      }));
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7058824f),
                        GColorF.newRGB(0.49803922f, 0.8039216f, 0.73333335f),
                        GColorF.newRGB(0.25490198f, 0.7137255f, 0.76862746f),
                        GColorF.newRGB(0.11372549f, 0.5686275f, 0.7529412f),
                        GColorF.newRGB(0.13333334f, 0.36862746f, 0.65882355f),
                        GColorF.newRGB(0.047058824f, 0.17254902f, 0.5176471f)
      }));
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8509804f),
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.69411767f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7058824f),
                        GColorF.newRGB(0.49803922f, 0.8039216f, 0.73333335f),
                        GColorF.newRGB(0.25490198f, 0.7137255f, 0.76862746f),
                        GColorF.newRGB(0.11372549f, 0.5686275f, 0.7529412f),
                        GColorF.newRGB(0.13333334f, 0.36862746f, 0.65882355f),
                        GColorF.newRGB(0.047058824f, 0.17254902f, 0.5176471f)
      }));
      add(new GColorScheme("YlGnBu", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8509804f),
                        GColorF.newRGB(0.92941177f, 0.972549f, 0.69411767f),
                        GColorF.newRGB(0.78039217f, 0.9137255f, 0.7058824f),
                        GColorF.newRGB(0.49803922f, 0.8039216f, 0.73333335f),
                        GColorF.newRGB(0.25490198f, 0.7137255f, 0.76862746f),
                        GColorF.newRGB(0.11372549f, 0.5686275f, 0.7529412f),
                        GColorF.newRGB(0.13333334f, 0.36862746f, 0.65882355f),
                        GColorF.newRGB(0.14509805f, 0.20392157f, 0.5803922f),
                        GColorF.newRGB(0.03137255f, 0.11372549f, 0.34509805f)
      }));


      // ColorScheme "YlOrBr"  Type=Sequential
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.7372549f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.05490196f)
      }));
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.96862745f, 0.7372549f),
                        GColorF.newRGB(0.99607843f, 0.76862746f, 0.30980393f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.05490196f)
      }));
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.83137256f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.5568628f),
                        GColorF.newRGB(0.99607843f, 0.6f, 0.16078432f),
                        GColorF.newRGB(0.8f, 0.29803923f, 0.007843138f)
      }));
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.83137256f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.5568628f),
                        GColorF.newRGB(0.99607843f, 0.6f, 0.16078432f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.05490196f),
                        GColorF.newRGB(0.6f, 0.20392157f, 0.015686275f)
      }));
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.83137256f),
                        GColorF.newRGB(0.99607843f, 0.8901961f, 0.5686275f),
                        GColorF.newRGB(0.99607843f, 0.76862746f, 0.30980393f),
                        GColorF.newRGB(0.99607843f, 0.6f, 0.16078432f),
                        GColorF.newRGB(0.8509804f, 0.37254903f, 0.05490196f),
                        GColorF.newRGB(0.6f, 0.20392157f, 0.015686275f)
      }));
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.83137256f),
                        GColorF.newRGB(0.99607843f, 0.8901961f, 0.5686275f),
                        GColorF.newRGB(0.99607843f, 0.76862746f, 0.30980393f),
                        GColorF.newRGB(0.99607843f, 0.6f, 0.16078432f),
                        GColorF.newRGB(0.9254902f, 0.4392157f, 0.078431375f),
                        GColorF.newRGB(0.8f, 0.29803923f, 0.007843138f),
                        GColorF.newRGB(0.54901963f, 0.1764706f, 0.015686275f)
      }));
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8980392f),
                        GColorF.newRGB(1.0f, 0.96862745f, 0.7372549f),
                        GColorF.newRGB(0.99607843f, 0.8901961f, 0.5686275f),
                        GColorF.newRGB(0.99607843f, 0.76862746f, 0.30980393f),
                        GColorF.newRGB(0.99607843f, 0.6f, 0.16078432f),
                        GColorF.newRGB(0.9254902f, 0.4392157f, 0.078431375f),
                        GColorF.newRGB(0.8f, 0.29803923f, 0.007843138f),
                        GColorF.newRGB(0.54901963f, 0.1764706f, 0.015686275f)
      }));
      add(new GColorScheme("YlOrBr", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8980392f),
                        GColorF.newRGB(1.0f, 0.96862745f, 0.7372549f),
                        GColorF.newRGB(0.99607843f, 0.8901961f, 0.5686275f),
                        GColorF.newRGB(0.99607843f, 0.76862746f, 0.30980393f),
                        GColorF.newRGB(0.99607843f, 0.6f, 0.16078432f),
                        GColorF.newRGB(0.9254902f, 0.4392157f, 0.078431375f),
                        GColorF.newRGB(0.8f, 0.29803923f, 0.007843138f),
                        GColorF.newRGB(0.6f, 0.20392157f, 0.015686275f),
                        GColorF.newRGB(0.4f, 0.14509805f, 0.023529412f)
      }));


      // ColorScheme "YlOrRd"  Type=Sequential
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.92941177f, 0.627451f),
                        GColorF.newRGB(0.9411765f, 0.23137255f, 0.1254902f)
      }));
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 0.92941177f, 0.627451f),
                        GColorF.newRGB(0.99607843f, 0.69803923f, 0.29803923f),
                        GColorF.newRGB(0.9411765f, 0.23137255f, 0.1254902f)
      }));
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.69803923f),
                        GColorF.newRGB(0.99607843f, 0.8f, 0.36078432f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f)
      }));
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.69803923f),
                        GColorF.newRGB(0.99607843f, 0.8f, 0.36078432f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.9411765f, 0.23137255f, 0.1254902f),
                        GColorF.newRGB(0.7411765f, 0.0f, 0.14901961f)
      }));
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.69803923f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.4627451f),
                        GColorF.newRGB(0.99607843f, 0.69803923f, 0.29803923f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.9411765f, 0.23137255f, 0.1254902f),
                        GColorF.newRGB(0.7411765f, 0.0f, 0.14901961f)
      }));
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.69803923f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.4627451f),
                        GColorF.newRGB(0.99607843f, 0.69803923f, 0.29803923f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.9882353f, 0.30588236f, 0.16470589f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.69411767f, 0.0f, 0.14901961f)
      }));
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(1.0f, 0.92941177f, 0.627451f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.4627451f),
                        GColorF.newRGB(0.99607843f, 0.69803923f, 0.29803923f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.9882353f, 0.30588236f, 0.16470589f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.69411767f, 0.0f, 0.14901961f)
      }));
      add(new GColorScheme("YlOrRd", GColorScheme.Type.Sequential, new IColor[] {
                        GColorF.newRGB(1.0f, 1.0f, 0.8f),
                        GColorF.newRGB(1.0f, 0.92941177f, 0.627451f),
                        GColorF.newRGB(0.99607843f, 0.8509804f, 0.4627451f),
                        GColorF.newRGB(0.99607843f, 0.69803923f, 0.29803923f),
                        GColorF.newRGB(0.99215686f, 0.5529412f, 0.23529412f),
                        GColorF.newRGB(0.9882353f, 0.30588236f, 0.16470589f),
                        GColorF.newRGB(0.8901961f, 0.101960786f, 0.10980392f),
                        GColorF.newRGB(0.7411765f, 0.0f, 0.14901961f),
                        GColorF.newRGB(0.5019608f, 0.0f, 0.14901961f)
      }));


   }


   //   public static void main(final String[] args) throws IOException {
   //
   //      //      final int dimensions = 9;
   //      //      final GColorScheme.Type type = GColorScheme.Type.Qualitative;
   //
   //      //      final String name = "Paired";
   //
   //      //      final DataOutputStream dos = new DataOutputStream(
   //      //               new FileOutputStream("color_schemes_" + dimensions + "_" + type + ".html"));
   //      //      final DataOutputStream dos = new DataOutputStream(new FileOutputStream("color_schemes_" + name + ".html"));
   //      final DataOutputStream dos = new DataOutputStream(new FileOutputStream("color_schemes.html"));
   //
   //      final GColorBrewerColorSchemeSet set = new GColorBrewerColorSchemeSet();
   //
   //      //      final List<GColorScheme> schemes = set.getSchemes(dimensions, type);
   //      final List<GColorScheme> schemes = set.getSchemes();
   //      for (final GColorScheme scheme : schemes) {
   //
   //         dos.writeChars("<h2>" + scheme.getName() + " -  " + scheme.getDimensions() + " - " + scheme.getType() + "</h2>");
   //
   //         dos.writeChars("<table><tr>");
   //         for (final IColor color : scheme.getColors()) {
   //            dos.writeChars("<td width=64 bgcolor=" + color.toHexString() + ">&nbsp;</td");
   //         }
   //         dos.writeChars("</tr></table>");
   //
   //         System.out.println(scheme);
   //      }
   //
   //      dos.close();
   //   }

}
