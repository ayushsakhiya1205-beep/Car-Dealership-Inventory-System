import React, { useState } from 'react';
import { X, RotateCcw, AlertCircle } from 'lucide-react';
import API from '../api/client';

const RestockModal = ({ vehicle, onClose, onSuccess }) => {
  const [quantity, setQuantity] = useState(10);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (quantity <= 0) {
      setError('Restock quantity must be positive');
      return;
    }

    setError('');
    setSubmitting(true);

    try {
      const response = await API.patch(`/api/vehicles/${vehicle.id}/restock`, {
        quantity: quantity
      });
      onSuccess(response.data);
    } catch (err) {
      console.error("Restocking failed:", err);
      const message = err.response?.data?.message || 'Failed to update stock';
      setError(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl w-full max-w-md overflow-hidden shadow-2xl animate-in fade-in zoom-in duration-200">
        {/* Header */}
        <div className="flex justify-between items-center bg-slate-950/40 px-6 py-4 border-b border-slate-800">
          <h3 className="text-lg font-bold text-white flex items-center space-x-2">
            <RotateCcw className="h-5 w-5 text-violet-400" />
            <span>Restock Inventory</span>
          </h3>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-200 cursor-pointer">
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && (
            <div className="flex items-center space-x-2 bg-rose-500/10 border border-rose-500/20 text-rose-400 p-3 rounded-xl text-sm">
              <AlertCircle className="h-4.5 w-4.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Current Stock info */}
          <div className="bg-slate-950/40 p-4 rounded-2xl border border-slate-800/80">
            <div className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-1">Vehicle Info</div>
            <div className="text-base font-bold text-white">{vehicle.year} {vehicle.make} {vehicle.model}</div>
            <div className="text-sm font-semibold text-slate-400 mt-1">Current Stock: {vehicle.stock}</div>
          </div>

          {/* Input field */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-400 mb-2">
              Units to Add
            </label>
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
              className="block w-full px-4 py-3 bg-slate-950/50 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:ring-2 focus:ring-violet-500/50 focus:border-violet-500 transition-all duration-300 text-sm"
              required
            />
          </div>

          {/* Action buttons */}
          <div className="grid grid-cols-2 gap-4 pt-2">
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
              {submitting ? 'Updating...' : 'Add Stock'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RestockModal;
