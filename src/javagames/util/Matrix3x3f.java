package javagames.util;


/**
 * Row major matrix
 */
public class Matrix3x3f {

    private float[][] m;

    public Matrix3x3f(float[][] m){
        this.m = m;
    }

    //sub
    public Matrix3x3f sub(Matrix3x3f obj){

        float[][] m1 = obj.m;

        return new Matrix3x3f(

            new float[][]{

                {m[0][0] - m1[0][0],
                m[0][1] - m1[0][1],
                m[0][2] - m1[0][2]},

                {m[1][0] - m1[1][0],
                m[1][1] - m1[1][1],
                m[1][2] - m1[1][2]},

                {m[2][0] - m1[2][0],
                m[2][1] - m1[2][1],
                m[2][2] - m1[2][2]}
            }
        );
    }// end of sub


    // add
    public Matrix3x3f add(Matrix3x3f obj){

        return new Matrix3x3f(

                new float[][]{

                    {m[0][0] + obj.m[0][0],
                            m[0][1] + obj.m[0][1],
                            m[0][2] + obj.m[0][2]},

                    {m[1][0] + obj.m[1][0],
                            m[1][1] + obj.m[1][1],
                            m[1][2] + obj.m[1][2]},

                    {m[2][0] + obj.m[2][0],
                            m[2][1] + obj.m[2][1],
                            m[2][2] + obj.m[2][2]},
                }

        );

    }// end of add


    //multiplication, order:  this.m X other.m
    public Matrix3x3f mul(Matrix3x3f obj){
        return new Matrix3x3f(new float[][] {
                {
                        m[0][0] * obj.m[0][0] + m[0][1] * obj.m[1][0] + m[0][2] * obj.m[2][0],
                        m[0][0] * obj.m[0][1] + m[0][1] * obj.m[1][1] + m[0][2] * obj.m[2][1],
                        m[0][0] * obj.m[0][2] + m[0][1] * obj.m[1][2] + m[0][2] * obj.m[2][2]
                },

                {
                        m[1][0] * obj.m[0][0] + m[1][1] * obj.m[1][0] + m[1][2] * obj.m[2][0],
                        m[1][0] * obj.m[0][1] + m[1][1] * obj.m[1][1] + m[1][2] * obj.m[2][1],
                        m[1][0] * obj.m[0][2] + m[1][1] * obj.m[1][2] + m[1][2] * obj.m[2][2]
                },

                {
                        m[2][0] * obj.m[0][0] + m[2][1] * obj.m[1][0] + m[2][2] * obj.m[2][0],
                        m[2][0] * obj.m[0][1] + m[2][1] * obj.m[1][1] + m[2][2] * obj.m[2][1],
                        m[2][0] * obj.m[0][2] + m[2][1] * obj.m[1][2] + m[2][2] * obj.m[2][2]
                }
        });
    }


    //Apply transform on a vector
    public Vector2f mul(Vector2f v) {
        float x = v.x * m[0][0] + v.y * m[1][0] + v.w * m[2][0];
        float y = v.x * m[0][1] + v.y * m[1][1] + v.w * m[2][1];
        float w = v.x * m[0][2] + v.y * m[1][2] + v.w * m[2][2];
        return new Vector2f(x, y, w);
    }


    //translate
    public static Matrix3x3f translate(Vector2f v){
        return translate(v.x, v.y);
    }

    public static Matrix3x3f translate(float x, float y) {
        return new Matrix3x3f(new float[][]{
                {1,0,0},
                {0,1,0},
                {x,y,1}
        });
    }// end of translate


    //Rotate
    public static Matrix3x3f rotate(float rad){
        return new Matrix3x3f(new float[][]{
                {(float) Math.cos(rad), (float) Math.sin(rad), 0},
                {(float)-Math.sin(rad), (float) Math.cos(rad), 0},
                {0, 0, 1}
        });
    }//end of rotate


    //Scale
    public static Matrix3x3f scale(Vector2f v) {
        return scale(v.x, v.y);
    }

    public static Matrix3x3f scale(float x, float y){
        return new Matrix3x3f(
                new float[][]{
                        {x, 0, 0},
                        {0, y, 0},
                        {0, 0, 1}
                }
        );
    }//end of scale

    //Shear
    public static Matrix3x3f shear(Vector2f v){
        return shear(v.x,v.y);
    }

    public static Matrix3x3f shear(float x, float y) {
        return new Matrix3x3f(
                new float[][]{
                        {1, y, 0},
                        {x, 1, 0},
                        {0, 0, 1}
                }
        );
    }//end of shear





    //Identity matrix
    public static Matrix3x3f identity() {
        return new Matrix3x3f(new float[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1},
        });
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < 3; i ++) {
            buff.append("[");
            buff.append(m[i][0]).append(" ").append(m[i][1]).append(" ").append(m[i][2]);
            buff.append("]");
            buff.append("\n");
        }

        return buff.toString();
    }
}