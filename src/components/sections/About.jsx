import React, { useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { gsap } from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import SectionTitle from '../ui/SectionTitle';
import GlassCard from '../ui/GlassCard';
import { Code2, Palette, Lightbulb, GraduationCap } from 'lucide-react';

gsap.registerPlugin(ScrollTrigger);

const cards = [
  {
    icon: GraduationCap,
    color: '#8b5cf6',
    title: 'Education',
    desc: 'Software Engineering student with a strong academic foundation in computer science and systems design.',
  },
  {
    icon: Code2,
    color: '#3b82f6',
    title: 'Development',
    desc: 'Builds robust systems with Java, MySQL, and Java Swing—including a real-world hotel inventory management system.',
  },
  {
    icon: Palette,
    color: '#06b6d4',
    title: 'Design',
    desc: 'Combines development with UI/UX using Figma, Adobe XD, and Canva to deliver clean, modern interfaces.',
  },
  {
    icon: Lightbulb,
    color: '#f59e0b',
    title: 'Problem Solver',
    desc: 'Focuses on solving real-world problems in education, business, and digital systems with practical solutions.',
  },
];

const About = () => {
  const sectionRef = useRef(null);
  const imageRef = useRef(null);

  useEffect(() => {
    const ctx = gsap.context(() => {
      gsap.fromTo(
        imageRef.current,
        { y: 60, opacity: 0, scale: 0.95 },
        {
          y: 0,
          opacity: 1,
          scale: 1,
          duration: 1,
          ease: 'power3.out',
          scrollTrigger: {
            trigger: sectionRef.current,
            start: 'top 75%',
          },
        }
      );
    }, sectionRef);
    return () => ctx.revert();
  }, []);

  return (
    <section
      id="about"
      ref={sectionRef}
      style={{
        padding: 'clamp(3rem, 6vw, 5rem) clamp(1rem, 5vw, 3rem)',
        position: 'relative',
      }}
    >
      {/* Background decorative elements */}
      <div
        style={{
          position: 'absolute',
          top: '10%',
          right: '-5%',
          width: '50vw',
          height: '50vh',
          background:
            'radial-gradient(ellipse at center, rgba(139,92,246,0.08) 0%, transparent 60%)',
          pointerEvents: 'none',
        }}
      />

      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <SectionTitle
          label="About Me"
          title="About Me"
          subtitle="From concept to code — building mobile & desktop apps that perform."
        />

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '1.5rem',
            alignItems: 'start',
          }}
        >
          {/* Info Cards */}
          <motion.div
            initial={{ opacity: 0, y: 40 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}
          >
            {cards.map(({ icon: Icon, color, title, desc }, index) => (
              <motion.div
                key={title}
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: index * 0.1 }}
              >
                <GlassCard
                  glow={false}
                  style={{
                    padding: '1.5rem',
                    background: 'rgba(255,255,255,0.03)',
                    border: `1px solid rgba(255,255,255,0.08)`,
                  }}
                >
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'flex-start',
                      gap: '1rem',
                    }}
                  >
                    <motion.div
                      whileHover={{ scale: 1.1, rotate: 5 }}
                      style={{
                        width: 48,
                        height: 48,
                        borderRadius: '14px',
                        background: `${color}15`,
                        border: `1px solid ${color}30`,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        flexShrink: 0,
                        backdropFilter: 'blur(10px)',
                      }}
                    >
                      <Icon size={22} style={{ color }} />
                    </motion.div>
                    <div>
                      <h4
                        style={{
                          fontFamily: 'Space Grotesk, sans-serif',
                          fontWeight: 600,
                          color: '#f1f5f9',
                          marginBottom: '0.35rem',
                          fontSize: '1rem',
                        }}
                      >
                        {title}
                      </h4>
                      <p
                        style={{
                          color: '#64748b',
                          fontSize: '0.875rem',
                          lineHeight: 1.65,
                        }}
                      >
                        {desc}
                      </p>
                    </div>
                  </div>
                </GlassCard>
              </motion.div>
            ))}
          </motion.div>

          {/* Visual Profile Card */}
          <motion.div
            ref={imageRef}
            initial={{ opacity: 0, scale: 0.9, y: 40 }}
            whileInView={{ opacity: 1, scale: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.7, delay: 0.2 }}
          >
            <GlassCard
              hover
              style={{
                padding: '2.5rem',
                background:
                  'linear-gradient(135deg, rgba(139,92,246,0.1), rgba(59,130,246,0.08))',
                border: '1px solid rgba(139,92,246,0.2)',
                borderRadius: '24px',
                textAlign: 'center',
                position: 'relative',
                overflow: 'hidden',
              }}
            >
              {/* Decorative gradient ring */}
              <div
                style={{
                  position: 'absolute',
                  top: '-50%',
                  left: '-50%',
                  width: '200%',
                  height: '200%',
                  background:
                    'conic-gradient(from 0deg at 50% 50%, transparent 0deg, rgba(139,92,246,0.05) 60deg, transparent 120deg)',
                  animation: 'spin 20s linear infinite',
                }}
              />

              {/* Avatar */}
              <div
                style={{
                  position: 'relative',
                  width: '140px',
                  height: '140px',
                  margin: '0 auto 1.5rem',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '3.5rem',
                  background:
                    'linear-gradient(135deg, rgba(139,92,246,0.3), rgba(59,130,246,0.3))',
                  border: '3px solid rgba(139,92,246,0.4)',
                  boxShadow:
                    '0 0 60px rgba(139,92,246,0.3), inset 0 0 30px rgba(139,92,246,0.1)',
                }}
              >
                <span role="img" aria-label="Developer">
                  👩‍💻
                </span>
              </div>

              <h3
                style={{
                  fontFamily: 'Space Grotesk, sans-serif',
                  fontSize: '1.75rem',
                  fontWeight: 700,
                  color: '#f1f5f9',
                  marginBottom: '0.5rem',
                  letterSpacing: '-0.02em',
                }}
              >
                Eman
              </h3>
              <p
                style={{
                  color: '#8b5cf6',
                  fontSize: '0.9375rem',
                  fontWeight: 500,
                  letterSpacing: '0.05em',
                  marginBottom: '1.5rem',
                }}
              >
                Software Developer
              </p>

              {/* Stats */}
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(3, 1fr)',
                  gap: '1rem',
                  marginBottom: '1.5rem',
                  padding: '1rem 0',
                  borderTop: '1px solid rgba(255,255,255,0.08)',
                  borderBottom: '1px solid rgba(255,255,255,0.08)',
                }}
              >
                {[
                  { num: '3+', label: 'Years' },
                  { num: '10+', label: 'Projects' },
                  { num: '5+', label: 'Technologies' },
                ].map((stat) => (
                  <div key={stat.num} style={{ textAlign: 'center' }}>
                    <div
                      style={{
                        fontSize: '1.75rem',
                        fontWeight: 700,
                        fontFamily: 'Space Grotesk, sans-serif',
                        background:
                          'linear-gradient(135deg, #8b5cf6, #3b82f6)',
                        WebkitBackgroundClip: 'text',
                        WebkitTextFillColor: 'transparent',
                        backgroundClip: 'text',
                      }}
                    >
                      {stat.num}
                    </div>
                    <div
                      style={{
                        color: '#64748b',
                        fontSize: '0.75rem',
                        textTransform: 'uppercase',
                        letterSpacing: '0.05em',
                      }}
                    >
                      {stat.label}
                    </div>
                  </div>
                ))}
              </div>

              <p
                style={{
                  color: '#64748b',
                  fontSize: '0.875rem',
                  lineHeight: 1.7,
                  marginBottom: '1.5rem',
                }}
              >
                Combining development with design—working with Java, React, React Native, and
                design tools to build solutions that make a difference.
              </p>

              {/* Tech tags */}
              <div
                style={{
                  display: 'flex',
                  flexWrap: 'wrap',
                  gap: '0.5rem',
                  justifyContent: 'center',
                }}
              >
                {[
                  'Java',
                  'React',
                  'Figma',
                  'MySQL',
                  'React Native',
                  'JavaScript',
                ].map((t) => (
                  <span
                    key={t}
                    style={{
                      fontSize: '0.75rem',
                      color: '#94a3b8',
                      background: 'rgba(255,255,255,0.05)',
                      border: '1px solid rgba(255,255,255,0.08)',
                      padding: '0.25rem 0.75rem',
                      borderRadius: '100px',
                    }}
                  >
                    {t}
                  </span>
                ))}
              </div>
            </GlassCard>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default About;
