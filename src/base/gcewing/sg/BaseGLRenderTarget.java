//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base for 1.8 - OpenGL rendering target
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;

import gcewing.sg.BaseModClient.*;

public class BaseGLRenderTarget extends BaseRenderTarget {

    public static boolean debugGL = false;

    protected boolean usingLightmap;
    protected int glMode;
    protected int emissiveMode;
    protected int texturedMode;

    // Cached GL normal/color — only emit GL call when value changes.
    protected float lastNx = Float.NaN, lastNy, lastNz;
    protected float lastR = Float.NaN, lastG, lastB, lastA;
    
    public BaseGLRenderTarget() {
        super(0, 0, 0, null);
    }
    
    public void start(boolean usingLightmap) {
        this.usingLightmap = usingLightmap;
        // Reset texture ref so setTexture() always calls glBindTexture on first use.
        // The shared static target carries this field across frames; without a reset,
        // a same-object texture comparison skips glBindTexture, leaving the wrong
        // texture bound after glPopAttrib restored the pre-start GL binding.
        texture = null;
        // Defensive GL state reset. glPushAttrib below does NOT cover GL_BLEND or
        // depth-mask; other TESRs or MC's render pipeline may leave them dirty.
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        if (debugGL) System.out.printf("BaseGLRenderTarget: glPushAttrib()\n");
        // GL_TEXTURE_BIT removed: glPopAttrib would silently restore the raw GL texture
        // binding behind GlStateManager's back, desyncing its cache and causing
        // subsequent TESRs to skip glBindTexture calls on unchanged-looking textures.
        glPushAttrib(GL_LIGHTING_BIT | GL_TRANSFORM_BIT);
        if (debugGL) System.out.printf("BaseGLRenderTarget: glEnable(GL_RESCALE_NORMAL)\n");
        glEnable(GL_RESCALE_NORMAL);
        glMode = 0;
        emissiveMode = -1;
        texturedMode = -1;
        // Invalidate cached GL state so first face always emits normal/color.
        lastNx = Float.NaN;
        lastR  = Float.NaN;
    }
    
    @Override
    public void setTexture(ITexture tex) {
        if (texture != tex) {
            super.setTexture(tex);
            ResourceLocation loc = tex.location();
            if (loc != null) {
                setGLMode(0);
                if (debugGL) System.out.printf("BaseGLRenderTarget: bindTexture(%s)\n", loc);
                BaseModClient.bindTexture(loc);
            }
            setTexturedMode(!tex.isSolid());
            setEmissiveMode(tex.isEmissive());
            // Texture tint affects r/g/b(); invalidate so emitColorIfChanged re-emits.
            lastR = Float.NaN;
        }
    }   
    
    protected void setEmissiveMode(boolean state) {
        int mode = state ? 1 : 0;
        if (emissiveMode != mode) {
            if (debugGL) System.out.printf("BaseGLRenderTarget: glSetEnabled(GL_LIGHTING, %s)\n", !state);
            glSetEnabled(GL_LIGHTING, !state);
            if (usingLightmap)
                setLightmapEnabled(!state);
            emissiveMode = mode;
        }
    }
    
    protected void setTexturedMode(boolean state) {
        int mode = state ? 1 : 0;
        if (texturedMode != mode) {
            //System.out.printf("BaseGLRenderTarget.setTexturedMode: %s\n", state);
            setGLMode(0);
            if (debugGL) System.out.printf("BaseGLRenderTarget: glSetEnabled(GL_TEXTURE_2D, %s)\n", state);
            glSetEnabled(GL_TEXTURE_2D, state);
            texturedMode = mode;
        }
    }

    protected void setLightmapEnabled(boolean state) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        glSetEnabled(GL_TEXTURE_2D, state);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    protected void glSetEnabled(int mode, boolean state) {
        if (state)
            glEnable(mode);
        else
            glDisable(mode);
    }

    @Override
    public void setNormal(Vector3 n) {
        super.setNormal(n);
        float nx = (float)n.x, ny = (float)n.y, nz = (float)n.z;
        if (nx != lastNx || ny != lastNy || nz != lastNz) {
            glNormal3f(nx, ny, nz);
            lastNx = nx; lastNy = ny; lastNz = nz;
        }
    }

    protected void emitColorIfChanged() {
        float r = r(), g = g(), b = b(), a = a();
        if (r != lastR || g != lastG || b != lastB || a != lastA) {
            glColor4f(r, g, b, a);
            lastR = r; lastG = g; lastB = b; lastA = a;
        }
    }

    @Override
    protected void rawAddVertex(Vector3 p, double u, double v) {
        setGLMode(verticesPerFace);
        emitColorIfChanged();
        glTexCoord2f((float)u, (float)v);
        if (debugGL) System.out.printf("BaseGLRenderTarget: glVertex3f%s\n", p);
        glVertex3f((float)p.x, (float)p.y, (float)p.z);
    }
    
    protected void setGLMode(int mode) {
        if (glMode != mode) {
            if (glMode != 0) {
                if (debugGL) System.out.printf("BaseGLRenderTarget: glEnd()\n");
                glEnd();
            }
            glMode = mode;
            switch (glMode) {
                case 0:
                    break;
                case 3:
                    if (debugGL) System.out.printf("BaseGLRenderTarget: glBegin(GL_TRIANGLES)\n");
                    glBegin(GL_TRIANGLES);
                    break;
                case 4:
                    if (debugGL) System.out.printf("BaseGLRenderTarget: glBegin(GL_QUADS)\n");
                    glBegin(GL_QUADS);
                    break;
                default:
                    throw new IllegalStateException(String.format("Invalid glMode %s", glMode));
            }
        }
    }
    
    @Override
    public void finish() {
        setGLMode(0);
        setEmissiveMode(false);
        setTexturedMode(true);
        if (debugGL) System.out.printf("BaseGLRenderTarget: glPopAttrib()\n");
        glPopAttrib();
        super.finish();
    }

}
