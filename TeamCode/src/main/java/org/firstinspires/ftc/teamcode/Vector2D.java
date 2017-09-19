package org.firstinspires.ftc.teamcode;

/**
 * Created by NoahR on 9/16/17.
 */

public class Vector2D {
    private double m_nXComponent;
    private double m_nYComponent;


    public Vector2D( Vector2D vecVector ) { Vector2D( vecVector.GetXComponent(), vecVector.GetYComponent() ); }
    public Vector2D( double nXComponent, double nYComponent ) 
    {
        m_nXComponent = nXComponent;
        m_nYComponent = nYComponent;
    }

    public double GetXComponent() { return m_nXComponent; }
    public double GetYComponent() { return m_nYComponent; }

    public double GetMagnitude()
    {
        return Math.sqrt( ( Math.pow(m_nXComponent, 2 ) ) + ( Math.pow(m_nYComponent, 2 ) ) );
    }
    public double GetDirection()
    {
        return Math.atan2( m_nYComponent, m_nXComponent );
    }

    public void Set( Vector2D vNewVector ) { this.SetComponents( vNewVector.GetXComponent(), vNewVector.GetYComponent() ); }
    public void SetPolar( double nMagnitude, double nDirection )
    {
        m_nXComponent = nMagnitude * Math.cos( nDirection );
        m_nYComponent = nMagnitude * Math.sin( nDirection );
    }
    public void SetComponents( double nXComponent, double nYComponent )
    {
        m_nXComponent = nXComponent;
        m_nYComponent = nYComponent;
    }

    public void Add( Vector2D vDelta ) { this.AddComponents( vDelta.GetXComponent(), vDelta.GetYComponent() ); }
    public void AddPolar( double nMagnitude, double nDirection )
    {
        m_nXComponent += nMagnitude * Math.cos( nDirection );
        m_nYComponent += nMagnitude * Math.sin( nDirection );
    }
    public void AddComponents( double nXComponent, double nYComponent )
    {
        m_nXComponent += nXComponent;
        m_nYComponent += nYComponent;
    }

    public void Multiply( Vector2D vVector ) { this.MultiplyComponents( vVector.GetXComponent(), vVector.GetYComponent() ); }
    public void MultiplyPolar( double nMagnitude, double nDirection )
    {
        m_nXComponent *= nMagnitude * Math.cos( nDirection );
        m_nYComponent *= nMagnitude * Math.sin( nDirection );
    }
    public void MultiplyComponents( double nXComponent, double nYComponent )
    {
        m_nXComponent *= nXComponent;
        m_nYComponent *= nYComponent;
    }

    public double DotProduct( Vector2D vVector )
    {
        return ( m_nXComponent * vVector.GetXComponent() ) + ( m_nYComponent * vVector.GetYComponent() );
    }
    
    public void MultiplyScalar( double nFactor )
    {
      m_nXComponent *= nFactor;
      m_nYComponent *= nFactor;
    }
}
