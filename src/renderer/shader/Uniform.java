package renderer.shader;

public enum Uniform {
	// standard matrices that should be used by any sane vertex shader
	U_MODEL_TO_WORLD_M4, U_MODEL_TO_VIEW_M4, U_MODEL_TO_PROJECTED_M4, U_WORLD_TO_PROJECTED_M4,

	U_EYE_WORLD_POS_3F,

	U_COLOR_MULT_F, U_TIME_F,

	// I don't yet know how to elegantly handle different light types
	// supported by shaders...
	U_POINT_LIGHT_1_3F, U_POINT_LIGHT_2_3F, U_POINT_LIGHT_3_3F,

	//
	U_ENV_CUBE, U_TEXTURE_1, U_TEXTURE_2,

	U_SHADOW_MAP_1, U_WORLD_TO_SHADOW_M4,
}