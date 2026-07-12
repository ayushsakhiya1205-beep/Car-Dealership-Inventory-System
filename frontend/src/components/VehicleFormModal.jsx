import React, { useState, useEffect } from 'react';
import { X, PlusCircle, Edit2, AlertCircle } from 'lucide-react';
import API from '../api/client';

const VehicleFormModal = ({ vehicle, onClose, onSuccess }) => {
  const isEditMode = !!vehicle;
  const [make, setMake] = useState('');
  const [model, setModel] = useState('');
  const [year, setYear] = useState(new Date().getFullYear());
  const [price, setPrice] = useState('');
  const [stock, setStock] = useState(0);
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Sedan');

  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (vehicle) {
      setMake(vehicle.make);
      setModel(vehicle.model);
      setYear(vehicle.year);
      setPrice(vehicle.price.toString());
      setStock(vehicle.stock);
      setDescription(vehicle.description || '');
      setCategory(vehicle.category || 'Sedan');
    }
  }, [vehicle]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!make || !model || year < 1886 || parseFloat(price) <= 0 || stock < 0) {
      setError('Please provide valid inputs for all required fields');
      return;
    }

    setError('');
    setSubmitting(true);

    const payload = {
      make,
      model,
      year: parseInt(year),
      price: parseFloat(price),
      stock: parseInt(stock),
      description,
      category
    };

    try {
      let response;
      if (isEditMode) {
        response = await API.put(`/api/vehicles/${vehicle.id}`, payload);
      } else {
        response = await API.post('/api/vehicles', payload);
      }
      onSuccess(response.data);
    } catch (err) {
      console.error("Save failed:", err);
      const message = err.response?.data?.message || 'Failed to save vehicle details';
      setError(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl w-full max-w-lg overflow-hidden shadow-2xl animate-in fade-in zoom-in duration-200">
        {/* Header */}
        <div className="flex justify-between items-center bg-slate-950/40 px-6 py-4 border-b border-slate-800">
          <h3 className="text-lg font-bold text-white flex items-center space-x-2">
            {isEditMode ? (
              <>
                <Edit2 className="h-5 w-5 text-violet-400" />
                <span>Edit Vehicle Details</span>
              </>
            ) : (
              <>
                <PlusCircle className="h-5 w-5 text-violet-400" />
                <span>Add Vehicle to Inventory</span>
              </>
            )}
          </h3>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-200 cursor-pointer">
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4 max-h-[80vh] overflow-y-auto">
          {error && (
            <div className="flex items-center space-x-2 bg-rose-500/10 border border-rose-500/20 text-rose-400 p-3 rounded-xl text-sm">
              <AlertCircle className="h-4.5 w-4.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Make */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Make *
              </label>
              <input
                type="text"
                value={make}
                onChange={(e) => setMake(e.target.value)}
                placeholder="e.g. Toyota"
                className="block w-full px-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 text-sm"
                required
              />
            </div>

            {/* Model */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Model *
              </label>
              <input
                type="text"
                value={model}
                onChange={(e) => setModel(e.target.value)}
                placeholder="e.g. Camry"
                className="block w-full px-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 text-sm"
                required
              />
            </div>

            {/* Category */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Category *
              </label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="block w-full px-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 text-sm"
                required
              >
                <option value="Sedan">Sedan</option>
                <option value="SUV">SUV</option>
                <option value="Electric">Electric</option>
                <option value="Sports">Sports</option>
                <option value="Hybrid">Hybrid</option>
                <option value="Truck">Truck</option>
                <option value="Other">Other</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            {/* Year */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Year *
              </label>
              <input
                type="number"
                min="1886"
                max={new Date().getFullYear() + 2}
                value={year}
                onChange={(e) => setYear(parseInt(e.target.value) || new Date().getFullYear())}
                className="block w-full px-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 text-sm"
                required
              />
            </div>

            {/* Price */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Price ($) *
              </label>
              <input
                type="number"
                step="0.01"
                min="0.01"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                placeholder="Price"
                className="block w-full px-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 text-sm"
                required
              />
            </div>

            {/* Stock */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
                Initial Stock *
              </label>
              <input
                type="number"
                min="0"
                value={stock}
                onChange={(e) => setStock(parseInt(e.target.value) || 0)}
                className="block w-full px-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 text-sm"
                required
              />
            </div>
          </div>

          {/* Description */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-1.5">
              Description
            </label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Provide a detailed description of the car..."
              rows="4"
              className="block w-full px-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 text-sm resize-none"
            />
          </div>

          {/* Buttons */}
          <div className="grid grid-cols-2 gap-4 pt-4 border-t border-slate-800/80">
            <button
              type="button"
              onClick={onClose}
              className="w-full py-3 bg-slate-850 hover:bg-slate-800 text-slate-300 font-semibold rounded-xl border border-slate-800 transition-colors duration-200 cursor-pointer text-sm"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="w-full py-3 bg-gradient-to-r from-violet-600 to-indigo-600 hover:from-violet-500 hover:to-indigo-500 text-white font-semibold rounded-xl shadow-lg shadow-violet-600/15 transition-all duration-300 cursor-pointer text-sm disabled:opacity-50"
            >
              {submitting ? 'Saving...' : isEditMode ? 'Save Changes' : 'Add Vehicle'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default VehicleFormModal;
