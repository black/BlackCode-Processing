class Heart
{
  // Lazy (Processing) class: leave direct access to parameters... Avoids having lot of setters.
  float m_x, m_y;	// Position of heart, the coordinates of the top
  int m_lineWidth;
  color m_colorLine;
  color m_colorFill;
  float m_size;	// Half width, the other factors are relative to this measure
  float m_proportion;	// Kind of ratio between half-width and distance between both sharp points
  float m_topFactor;	// Relative size of top vector (control handle)
  float m_bottomFactor;	// Relative size of vector of bottom point
  float m_bottomAngle;	// Angle of bottom vector, in degrees, relative to vertical axis

  private float m_topVectorLen;
  private float m_bottomLen;
  private float m_bottomVector_dx, m_bottomVector_dy;

  /**
   * Simple constructor with hopefully sensible defaults.
   */
  Heart(float x, float y, float size, color colorLine, color colorFill)
  {
    this(x, y, size, 1.5, 0.7, 0.5, 30.0, 1, colorLine, colorFill);
  }

  /**
   * Full constructor.
   */
  Heart(float x, float y, float size, float proportion,
      float topFactor, float bottomFactor, float bottomAngle,
      int lineWidth, color colorLine, color colorFill
  )
  {
    m_x = x; m_y = y;
    m_size = size;
    m_proportion = proportion;
    m_lineWidth = lineWidth;
    m_colorLine = colorLine;
    m_colorFill = colorFill;
    m_topFactor = topFactor;
    m_bottomFactor = bottomFactor;
    m_bottomAngle = bottomAngle;

    update();
  }

  /**
   * After setting parameters, must call this method.
   */
  void update()
  {
    m_topVectorLen = m_size * m_topFactor;
    m_bottomLen = m_size * m_proportion;
    float bottomAngle = (90 - m_bottomAngle) * PI / 180.0;
    float bottomVectorLength = m_size * m_bottomFactor;
    m_bottomVector_dx = cos(bottomAngle) * bottomVectorLength;
    m_bottomVector_dy = sin(bottomAngle) * bottomVectorLength;
  }

  void draw()
  {
    fill(m_colorFill);
    stroke(m_colorLine);
    strokeWeight(m_lineWidth);

    beginShape();
    // The heart, symmetrical around vertical axis
    // Anchor point (highest sharp point)
    vertex(m_x, m_y);
    // Top left part
    bezierVertex(
        m_x, m_y - m_topVectorLen,	// Upward control vector
        m_x - m_size, m_y - m_topVectorLen,	// Idem
        m_x - m_size, m_y);
    // Bottom left part
    bezierVertex(
        m_x - m_size, m_y + m_topVectorLen,	// Downward control vector
        m_x - m_bottomVector_dx, m_y + m_bottomLen - m_bottomVector_dy,
        m_x, m_y + m_bottomLen);
    // Bottom right part
    bezierVertex(
        m_x + m_bottomVector_dx, m_y + m_bottomLen - m_bottomVector_dy,
        m_x + m_size, m_y + m_topVectorLen,	// Downward control vector
        m_x + m_size, m_y);
    // Top right part
    bezierVertex(
        m_x + m_size, m_y - m_topVectorLen,	// Upward control vector
        m_x, m_y - m_topVectorLen,	// Idem
        m_x, m_y);

    endShape();
  }
}
