import React from 'react';
import { Linkedin, ExternalLink } from 'lucide-react';

const ProjectModal = ({ project, onClose }) => {
  if (!project) return null;
  
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button onClick={onClose} className="modal-close">
          <ExternalLink size={24} />
        </button>
        <h2>{project.title}</h2>
        <p>{project.description}</p>
        <div className="project-links">
          {project.github && (
            <a href={project.github} target="_blank" rel="noopener noreferrer">
              <Linkedin size={20} />
            </a>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProjectModal;