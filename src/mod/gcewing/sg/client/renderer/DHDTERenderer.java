//------------------------------------------------------------------------------------------------
//
//   SG Craft - DHD tile entity renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.client.renderer;

import gcewing.sg.BaseGLUtils;
import gcewing.sg.BaseModClient.IModel;
import gcewing.sg.BaseModClient.IRenderTarget;
import gcewing.sg.BaseModClient.ITexture;
import gcewing.sg.BaseModClient.ITiledTexture;
import gcewing.sg.BaseModel;
import gcewing.sg.BaseTexture.Image;
import gcewing.sg.BaseTileEntity;
import gcewing.sg.BaseTileEntityRenderer;
import gcewing.sg.tileentity.DHDTE;
import gcewing.sg.tileentity.SGBaseTE;
import gcewing.sg.SGCraft;
import gcewing.sg.Trans3;
import gcewing.sg.Vector3;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

public class DHDTERenderer extends BaseTileEntityRenderer {

    IModel model;
    ITexture mainTexture;
    ITexture[] milkywayButtonTextures, pegasusButtonTextures;
    ITexture[] textures;

    // [gateType 0=milkyway 1=pegasus][buttonState 0=no-gate 1=inactive 2=active]
    // 0 = not yet compiled
    private final int[][] displayLists = new int[2][3];

    final static int buttonTextureIndex = 3;
    
    public DHDTERenderer() {
        SGCraft mod = SGCraft.mod;
        ResourceLocation ttLoc = mod.textureLocation("tileentity/dhd_top.png");
        ResourceLocation stLoc = mod.textureLocation("tileentity/dhd_side.png");
        ResourceLocation dtLoc = mod.textureLocation("tileentity/dhd_detail.png");
        ITiledTexture detail = new Image(dtLoc).tiled(2, 2);
        textures = new ITexture[] {
            new Image(ttLoc),
            new Image(stLoc),
            detail.tile(1, 1),
            null, // button texture inserted here
        };
        ITexture button = detail.tile(0, 0);
        milkywayButtonTextures = new ITexture[] {
            button.colored(0.5, 0.5,  0.5),
            button.colored(0.5, 0.25, 0.0),
            button.colored(1.0, 0.5, 0.0).emissive(),
        };
        pegasusButtonTextures = new ITexture[] {
            button.colored(0.0, 0.5,  0.5),
            button.colored(0.0, 0.25, 0.75),
            button.colored(0.0, 0.5, 1.0).emissive(),
        };
        model = BaseModel.fromResource(mod.resourceLocation("models/block/dhd.smeg"));
        DHDTE.bounds = model.getBounds();
    }

    @Override
    public void render(TileEntity tileEntity, double x, double y, double z,
            float partialTicks, int destroyStage, float alpha) {
        DHDTE dte = (DHDTE) tileEntity;
        SGBaseTE gte = dte.getLinkedStargateTE();

        int gateType = (gte != null && gte.gateType == 2) ? 1 : 0;
        int buttonState;
        if (gte == null)
            buttonState = 0;
        else if (gte.isActive())
            buttonState = 2;
        else
            buttonState = 1;

        if (displayLists[gateType][buttonState] == 0)
            displayLists[gateType][buttonState] = compileList(gateType, buttonState);

        Trans3 t = dte.localToGlobalTransformation(Vector3.blockCenter(x, y, z)).translate(0, -0.5, 0);
        glPushMatrix();
        BaseGLUtils.glMultMatrix(t);
        glCallList(displayLists[gateType][buttonState]);
        glPopMatrix();
    }

    private int compileList(int gateType, int buttonState) {
        ITexture[] buttonSet = (gateType == 1) ? pegasusButtonTextures : milkywayButtonTextures;
        textures[buttonTextureIndex] = buttonSet[buttonState];

        // Pre-load every texture through MC's TextureManager BEFORE entering GL_COMPILE.
        // In GL_COMPILE mode all GL commands are recorded but not executed, so texture
        // upload would be baked with wrong IDs. Binding here ensures all textures are
        // already on the GPU before we compile.
        for (ITexture tex : textures) {
            if (tex != null) {
                ResourceLocation loc = tex.location();
                if (loc != null) gcewing.sg.BaseModClient.bindTexture(loc);
            }
        }

        // Fresh target: shared static target carries a stale texture ref across frames;
        // setTexture() skips glBindTexture when it thinks tex is already set.
        gcewing.sg.BaseGLRenderTarget compileTarget = new gcewing.sg.BaseGLRenderTarget();

        int id = glGenLists(1);
        glNewList(id, GL_COMPILE);
        compileTarget.start(true);
        model.render(Trans3.ident, compileTarget, textures);
        compileTarget.finish();
        glEndList();
        return id;
    }

    public void render(BaseTileEntity te, float dt, int destroyStage, Trans3 t, IRenderTarget target) {
        DHDTE dte = (DHDTE)te;
        SGBaseTE gte = dte.getLinkedStargateTE();
        int i;
        if (gte == null)
            i = 0;
        else if (gte.isActive())
            i = 2;
        else
            i = 1;
        if (gte == null) {
            textures[buttonTextureIndex] = milkywayButtonTextures[i];
        } else {
            if (gte.gateType == 2) {
                textures[buttonTextureIndex] = pegasusButtonTextures[i];
            } else {
                textures[buttonTextureIndex] = milkywayButtonTextures[i];
            }
        }
        model.render(t.translate(0, -0.5, 0), target, textures);
    }
}
