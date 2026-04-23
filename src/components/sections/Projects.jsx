import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import SectionTitle from '../ui/SectionTitle';
import ProjectModal from '../ui/ProjectModal';
import { ExternalLink, ArrowUpRight } from 'lucide-react';
import { FaGithub } from 'react-icons/fa';

const projects = [
  {
    emoji: '📦',
    title: 'Inventory Management System',
    category: 'Desktop Application',
    desc: 'A full-featured desktop app for managing inventory, stock tracking, and reporting.',
    longDesc: 'A comprehensive Inventory Management System built as a desktop application. It supports product tracking, stock level monitoring, purchase/sale records, and detailed reporting. Designed for real-world business use with an intuitive interface and reliable database integration.',
    tech: ['Java', 'Java Swing', 'MySQL', 'JDBC'],
    color: '#8b5cf6',
    gradient: 'linear-gradient(135deg, rgba(139,92,246,0.25), rgba(124,58,237,0.08))',
    github: 'https://github.com/eman-110/InventoryManagmentSystem',
  },
  {
    emoji: '🚀',
    title: 'CareerWeg',
    category: 'Mobile Application',
    desc: 'A career guidance mobile app helping users explore job paths and opportunities.',
    longDesc: 'CareerWeg is a mobile application designed to guide users through their career journey. It provides personalized career recommendations, job exploration tools, and resources to help users make informed decisions about their professional future.',
    tech: ['React Native', 'JavaScript', 'Firebase'],
    color: '#3b82f6',
    gradient: 'linear-gradient(135deg, rgba(59,130,246,0.25), rgba(37,99,235,0.08))',
    github: 'https://github.com/eman-110/CareerWeg',
  },
  {
    emoji: '🔒',
    title: 'SecureScan',
    category: 'Security Tool',
    desc: 'A security scanning tool designed to detect vulnerabilities and enhance app safety.',
    longDesc: 'SecureScan is a powerful security-focused application that scans systems or applications for vulnerabilities, potential threats, and weaknesses. Built to help developers and security professionals identify risks and apply fixes proactively.',
    tech: ['Python', 'Security', 'Networking'],
    color: '#06b6d4',
    gradient: 'linear-gradient(135deg, rgba(6,182,212,0.25), rgba(8,145,178,0.08))',
    github: 'https://github.com/eman-110/SecureScan',
  },
];

const Projects = () => {
  const [selected, setSelected] = useState(null);
  const [hovered, setHovered] = useState(null);

  const containerVariants = {
    hidden: {},
    visible: { transition: { staggerChildren: 0.12 } },
  };

  const cardVariants = {
    hidden: { opacity: 0, y: 60, scale: 0.95 },
    visible: { opacity: 1, y: 0, scale: 1, transition: { duration: 0.6, ease: 'easeOut' } },
  };

  return (
    <section
      id="projects"
      style={{
        padding: 'clamp(3rem, 6vw, 5rem) clamp(1rem, 5vw, 3rem)',
        position: 'relative',
      }}
    >
      {/* Background glow */}
      <div
        style={{
          position: 'absolute',
          bottom: '-10%',
          left: '20%',
          width: '60vw',
          height: '60vh',
          background:
            'radial-gradient(ellipse at center, rgba(59,130,246,0.08) 0%, transparent 60%)',
          pointerEvents: 'none',
        }}
      />

      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <SectionTitle
          label="Projects"
          title="Featured Work"
          subtitle="Real-world applications and design projects showcasing technical expertise and creative problem-solving"
        />

        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, margin: '-60px' }}
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
            gap: '1.75rem',
          }}
        >
          {projects.map((project) => (
            <motion.div
              key={project.title}
              variants={cardVariants}
              onHoverStart={() => setHovered(project.title)}
              onHoverEnd={() => setHovered(null)}
            >
              <motion.div
                layoutId={`card-${project.title}`}
                whileHover={{ y: -12, scale: 1.02 }}
                transition={{ type: 'spring', stiffness: 280, damping: 22 }}
                style={{
                  background: hovered === project.title ? project.gradient : 'rgba(255,255,255,0.03)',
                  border: `1px solid ${hovered === project.title ? project.color + '50' : 'rgba(255,255,255,0.08)'}`,
                  borderRadius: '20px',
                  overflow: 'hidden',
                  position: 'relative',
                  cursor: 'pointer',
                  transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
                  boxShadow: hovered === project.title
                    ? `0 30px 80px ${project.color}25`
                    : '0 4px 20px rgba(0,0,0,0.2)',
                }}
              >
                {/* Banner with overlay */}
                <div
                  style={{
                    position: 'relative',
                    height: '160px',
                    background: project.gradient,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '3.5rem',
                    overflow: 'hidden',
                  }}
                >
                  {/* Grid pattern */}
                  <div
                    style={{
                      position: 'absolute',
                      inset: 0,
                      backgroundImage: `linear-gradient(rgba(255,255,255,0.04) 1px,transparent 1px),linear-gradient(90deg,rgba(255,255,255,0.04) 1px,transparent 1px)`,
                      backgroundSize: '24px 24px',
                    }}
                  />

                  {/* Project emoji */}
                  <motion.div
                    animate={hovered === project.title ? { scale: 1.2, rotate: 5 } : { scale: 1, rotate: 0 }}
                    transition={{ type: 'spring', stiffness: 300, damping: 15 }}
                  >
                    {project.emoji}
                  </motion.div>

                  {/* Overlay */}
                  <AnimatePresence>
                    {hovered === project.title && (
                      <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        style={{
                          position: 'absolute',
                          inset: 0,
                          background: 'rgba(0,0,0,0.4)',
                          backdropFilter: 'blur(2px)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          color: '#fff',
                          fontSize: '0.75rem',
                          fontWeight: 700,
                          letterSpacing: '0.1em',
                          textTransform: 'uppercase',
                        }}
                      >
                        View Details
                      </motion.div>
                    )}
                  </AnimatePresence>
                </div>

                {/* Content */}
                <div style={{ padding: '1.5rem' }}>
                  {/* Category tag */}
                  <div
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      gap: '0.5rem',
                      marginBottom: '0.75rem',
                      padding: '0.35rem 0.875rem',
                      background: `${project.color}20`,
                      border: `1px solid ${project.color}40`,
                      borderRadius: '100px',
                    }}
                  >
                    <span
                      style={{
                        fontSize: '0.65rem',
                        fontWeight: 600,
                        letterSpacing: '0.1em',
                        textTransform: 'uppercase',
                        color: project.color,
                      }}
                    >
                      {project.category}
                    </span>
                  </div>

                  {/* Title & Description */}
                  <h3
                    style={{
                      fontFamily: 'Space Grotesk, sans-serif',
                      fontWeight: 700,
                      fontSize: '1.125rem',
                      color: '#f1f5f9',
                      marginBottom: '0.5rem',
                      lineHeight: 1.3,
                    }}
                  >
                    {project.title}
                  </h3>
                  <p
                    style={{
                      color: '#64748b',
                      fontSize: '0.875rem',
                      lineHeight: 1.65,
                      marginBottom: '1rem',
                    }}
                  >
                    {project.desc}
                  </p>

                  {/* Tech stack */}
                  <div
                    style={{
                      display: 'flex',
                      flexWrap: 'wrap',
                      gap: '0.5rem',
                      marginBottom: '1.25rem',
                    }}
                  >
                    {project.tech.map((t) => (
                      <span
                        key={t}
                        style={{
                          fontSize: '0.7rem',
                          color: '#94a3b8',
                          background: 'rgba(255,255,255,0.05)',
                          border: '1px solid rgba(255,255,255,0.08)',
                          padding: '0.25rem 0.625rem',
                          borderRadius: '100px',
                        }}
                      >
                        {t}
                      </span>
                    ))}
                  </div>

                  {/* Action buttons */}
                  <div style={{ display: 'flex', gap: '0.75rem' }}>
                    <motion.a
                      href={project.github}
                      onClick={(e) => e.stopPropagation()}
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.5rem',
                        padding: '0.5rem 1rem',
                        background: 'rgba(255,255,255,0.06)',
                        border: '1px solid rgba(255,255,255,0.12)',
                        borderRadius: '10px',
                        color: '#94a3b8',
                        textDecoration: 'none',
                        fontSize: '0.8125rem',
                        fontWeight: 500,
                        transition: 'all 0.2s ease',
                        backdropFilter: 'blur(10px)',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.background = 'rgba(255,255,255,0.1)';
                        e.currentTarget.style.color = '#fff';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.background = 'rgba(255,255,255,0.06)';
                        e.currentTarget.style.color = '#94a3b8';
                      }}
                    >
                      <FaGithub size={14} />
                      Code
                    </motion.a>
                    <motion.a
                      href="#"
                      onClick={(e) => e.stopPropagation()}
                      whileHover={{ scale: 1.05 }}
                      whileTap={{ scale: 0.95 }}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.5rem',
                        padding: '0.5rem 1rem',
                        background: `linear-gradient(135deg, ${project.color}, ${project.color}cc)`,
                        border: 'none',
                        borderRadius: '10px',
                        color: '#fff',
                        textDecoration: 'none',
                        fontSize: '0.8125rem',
                        fontWeight: 600,
                        transition: 'all 0.2s ease',
                      }}
                    >
                      <ExternalLink size={14} />
                      Live Demo
                    </motion.a>
                  </div>
                </div>
              </motion.div>
            </motion.div>
          ))}
        </motion.div>

        {/* View More Button */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ delay: 0.6 }}
          style={{ textAlign: 'center', marginTop: '3rem' }}
        >
          <motion.a
            href="https://github.com/eman-110"
            target="_blank"
            rel="noopener noreferrer"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.875rem 2rem',
              background: 'rgba(255,255,255,0.05)',
              border: '1px solid rgba(255,255,255,0.12)',
              borderRadius: '12px',
              color: '#e2e8f0',
              textDecoration: 'none',
              fontSize: '0.9375rem',
              fontWeight: 500,
              backdropFilter: 'blur(10px)',
              transition: 'all 0.3s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = 'rgba(139, 92, 246, 0.15)';
              e.currentTarget.style.borderColor = 'rgba(139, 92, 246, 0.4)';
              e.currentTarget.style.color = '#fff';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = 'rgba(255,255,255,0.05)';
              e.currentTarget.style.borderColor = 'rgba(255,255,255,0.12)';
              e.currentTarget.style.color = '#e2e8f0';
            }}
          >
            <FaGithub size={18} />
            View All Projects on GitHub
            <ArrowUpRight size={16} />
          </motion.a>
        </motion.div>
      </div>

      <AnimatePresence>{selected && <ProjectModal project={selected} onClose={() => setSelected(null)} />}</AnimatePresence>
    </section>
  );
};

export default Projects;
