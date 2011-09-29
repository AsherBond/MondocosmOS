//////////// ATTRIBUTES ////////////
attribute vec4 boneIndices;

///////////// VARYING /////////////
varying vec3 VNormal;

////////////  UNIFORMS ////////////
uniform mat4 pose[55];

///////////// GLOBALS /////////////
vec4 Position;
mat4 poseBlend;

//////////// PROTOTYPES ///////////
void SimpleFTransform_Transfom();
void VertexDeformer_Transform();

/////////// MAIN LOGIC ////////////
void main(void)
{
	SimpleFTransform_Transfom();
        VertexDeformer_Transform();
	gl_Position = gl_ModelViewProjectionMatrix * Position;
}

/******************************************
* Function: SimpleFTransform_Transfom
*******************************************/
void SimpleFTransform_Transfom()
{
	Position = gl_Vertex;
}

/******************************************
* Function: VertexDeformer_Transform
*******************************************/
void VertexDeformer_Transform()
{
        vec3 weight = gl_Color.rgb;
	float weight4 = 1.0 - (weight.x + weight.y + weight.z);
	poseBlend = (pose[int(boneIndices.x)]) * weight.x +
	(pose[int(boneIndices.y)]) * weight.y +
	(pose[int(boneIndices.z)]) * weight.z +
	(pose[int(boneIndices.w)]) * weight4;
	VNormal.x = dot(gl_Normal, poseBlend[0].xyz);
	VNormal.y = dot(gl_Normal, poseBlend[1].xyz);
	VNormal.z = dot(gl_Normal, poseBlend[2].xyz);
	Position = Position * poseBlend;
}